package io.allpad.stripe.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Invoice;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Subscription;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.SubscriptionCancelParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionRetrieveParams;
import com.stripe.param.SubscriptionUpdateParams;
import io.allpad.auth.entity.User;
import io.allpad.auth.utils.ContextUtils;
import io.allpad.stripe.config.StripeProperties;
import io.allpad.stripe.dto.StripeSubscriptionDTO;
import io.allpad.stripe.entity.StripeSubscription;
import io.allpad.stripe.error.StripeSubscriptionException;
import io.allpad.stripe.repository.StripeSubscriptionRepository;
import io.allpad.stripe.service.StripeSubscriptionService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeSubscriptionServiceImpl implements StripeSubscriptionService {
    private final StripeProperties stripeProperties;
    private final StripeSubscriptionRepository stripeSubscriptionRepository;
    private final ContextUtils contextUtils;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeProperties.secretKey();
    }

    @Override
    public StripeSubscriptionDTO createSubscription(StripeSubscriptionDTO stripeSubscriptionDTO) {
        try {
            var user = contextUtils.getUser();
            var customerId = findOrCreateCustomer(user);
            var existingSubscription = stripeSubscriptionRepository.findByUser(user);
            Subscription subscription;
            if (existingSubscription.isPresent()) {
                var stripeSubscription = existingSubscription.get();
                subscription = getSubscription(stripeSubscription.getStripeSubscriptionId());
                var priceId = subscription.getItems().getData().getLast().getPrice().getId();
                if ("active".equals(subscription.getStatus()) && priceId.equals(stripeSubscription.getPriceId())) {
                    log.info("User already has the same active subscription");
                } else if ("incomplete".equals(subscription.getStatus())
                        && priceId.equals(stripeSubscription.getPriceId())) {
                    log.info("Retrying the payment subscription");
                } else if ("canceled".equals(subscription.getStatus())) {
                    subscription = createSubscription(customerId, stripeSubscriptionDTO);
                } else {
                    subscription = downgradeUpgradeSubscription(subscription, stripeSubscriptionDTO);
                }
            } else {
                subscription = createSubscription(customerId, stripeSubscriptionDTO);
            }
            var stripeSubscription = existingSubscription.orElse(new StripeSubscription());
            stripeSubscription.setUser(user);
            stripeSubscription.setStripeCustomerId(customerId);
            stripeSubscription.setStripeSubscriptionId(subscription.getId());
            stripeSubscription.setPlanId(stripeSubscriptionDTO.planId());
            stripeSubscription.setPriceId(stripeSubscriptionDTO.priceId());
            stripeSubscription.setStatus(subscription.getStatus());
            stripeSubscription.setCurrentPeriodEnd(subscription.getItems().getData().getLast().getCurrentPeriodEnd());
            stripeSubscriptionRepository.save(stripeSubscription);
            var paymentIntentId = subscription.getLatestInvoiceObject().getPayments().getData().getLast().getPayment()
                    .getPaymentIntent();
            var paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return StripeSubscriptionDTO.builder()
                    .planId(stripeSubscription.getPlanId())
                    .priceId(stripeSubscription.getPriceId())
                    .subscriptionId(stripeSubscription.getStripeSubscriptionId())
                    .clientSecret(paymentIntent.getClientSecret())
                    .status(stripeSubscription.getStatus())
                    .build();
        } catch (StripeException e) {
            throw new StripeSubscriptionException(String.format("Failed to create subscription: %s", e.getMessage()), e);
        }
    }

    private Subscription getSubscription(String subscriptionId) throws StripeException {
        var params = SubscriptionRetrieveParams.builder()
                .addAllExpand(List.of("latest_invoice.payments.data.payment"))
                .build();
        return Subscription.retrieve(subscriptionId, params, null);
    }

    private Subscription createSubscription(String customerId, StripeSubscriptionDTO stripeSubscriptionDTO)
            throws StripeException {
        var params = SubscriptionCreateParams.builder()
                .setCustomer(customerId)
                .addItem(
                        SubscriptionCreateParams.Item.builder()
                                .setPrice(stripeSubscriptionDTO.priceId())
                                .build())
                .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
                .addAllExpand(List.of("latest_invoice.payments.data.payment"))
                .build();
        return Subscription.create(params);
    }

    private Subscription downgradeUpgradeSubscription(Subscription subscription,
            StripeSubscriptionDTO stripeSubscriptionDTO) throws StripeException {
        var params = SubscriptionUpdateParams.builder()
                .addItem(
                        SubscriptionUpdateParams.Item.builder()
                                .setId(stripeSubscriptionDTO.subscriptionId())
                                .setPrice(stripeSubscriptionDTO.priceId())
                                .build())
                .setProrationBehavior(SubscriptionUpdateParams.ProrationBehavior.CREATE_PRORATIONS)
                .setPaymentBehavior(SubscriptionUpdateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
                .addAllExpand(List.of("latest_invoice.payments.data.payment"))
                .build();
        return subscription.update(params);
    }

    @Override
    public void cancelSubscription() {
        try {
            var user = contextUtils.getUser();
            var stripeSubscription = stripeSubscriptionRepository.findByUser(user)
                    .orElseThrow(() -> new StripeSubscriptionException("Subscription not found"));
            var subscription = Subscription.retrieve(stripeSubscription.getStripeSubscriptionId());
            var params = SubscriptionCancelParams.builder().build();
            var updatedSubscription = subscription.cancel(params);
            stripeSubscription.setStatus(updatedSubscription.getStatus());
            stripeSubscriptionRepository.save(stripeSubscription);
        } catch (StripeException e) {
            throw new StripeSubscriptionException(String.format("Failed to cancel subscription: %s", e.getMessage()), e);
        }
    }

    @Override
    public void handleWebhook(String payload, String sigHeader) {
        try {
            var event = Webhook.constructEvent(payload, sigHeader, stripeProperties.webhookSecret());
            var stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
            if (stripeObject == null) {
                return;
            }
            switch (event.getType()) {
                case "invoice.payment_succeeded":
                    handlePaymentSucceeded((Invoice) stripeObject);
                    break;
                case "customer.subscription.updated":
                    handleSubscriptionUpdated((Subscription) stripeObject);
                    break;
                case "customer.subscription.deleted":
                    handleSubscriptionDeleted((Subscription) stripeObject);
                    break;
                default:
                    log.info("Unhandled event type: {}", event.getType());
            }
        } catch (Exception e) {
            throw new StripeSubscriptionException(String.format("Webhook verification failed: %s", e.getMessage()), e);
        }
    }

    private void handlePaymentSucceeded(Invoice invoice) {
        log.info("Handling payment succeeded for invoice: {}, {}, {}",
                invoice.getId(), invoice.getCustomer(), invoice.getStatus());
        var subscriptionOpt = stripeSubscriptionRepository.findByStripeCustomerId(invoice.getCustomer());
        if (subscriptionOpt.isPresent()) {
            var stripeSubscription = subscriptionOpt.get();
            stripeSubscription.setInvoiceStatus(invoice.getStatus());
            stripeSubscriptionRepository.save(stripeSubscription);
        }
    }

    private void handleSubscriptionUpdated(Subscription subscription) {
        log.info("Handling subscription updated for subscription: {}, {}, {}",
                subscription.getId(), subscription.getCustomer(), subscription.getStatus());
        var subscriptionOpt = stripeSubscriptionRepository.findByStripeSubscriptionId(subscription.getId());
        if (subscriptionOpt.isPresent()) {
            var stripeSubscription = subscriptionOpt.get();
            stripeSubscription.setStatus(subscription.getStatus());
            stripeSubscription.setCurrentPeriodEnd(subscription.getItems().getData().getLast().getCurrentPeriodEnd());
            stripeSubscriptionRepository.save(stripeSubscription);
        }
    }

    private void handleSubscriptionDeleted(Subscription subscription) {
        log.info("Handling subscription deleted for subscription: {}, {}, {}",
                subscription.getId(), subscription.getCustomer(), subscription.getStatus());
        var subscriptionOpt = stripeSubscriptionRepository.findByStripeSubscriptionId(subscription.getId());
        if (subscriptionOpt.isPresent()) {
            var stripeSubscription = subscriptionOpt.get();
            stripeSubscription.setStatus(subscription.getStatus());
            stripeSubscription.setCurrentPeriodEnd(subscription.getItems().getData().getLast().getCurrentPeriodEnd());
            stripeSubscriptionRepository.save(stripeSubscription);
        }
    }

    private String findOrCreateCustomer(User user) throws StripeException {
        var existingSub = stripeSubscriptionRepository.findByUser(user);
        if (existingSub.isPresent() && existingSub.get().getStripeCustomerId() != null) {
            return existingSub.get().getStripeCustomerId();
        }
        var params = CustomerSearchParams.builder()
                .setQuery("email:'" + user.getEmail() + "'")
                .build();
        var customers = Customer.search(params);
        if (!customers.getData().isEmpty()) {
            return customers.getData().getLast().getId();
        }
        var createParams = CustomerCreateParams.builder()
                .setEmail(user.getEmail())
                .setName(user.getName() + " " + user.getLastName())
                .build();
        var customer = Customer.create(createParams);
        return customer.getId();
    }
}
