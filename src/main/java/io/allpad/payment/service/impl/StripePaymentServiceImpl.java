package io.allpad.payment.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Invoice;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.SubscriptionCancelParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionRetrieveParams;
import com.stripe.param.SubscriptionUpdateParams;
import io.allpad.auth.entity.User;
import io.allpad.payment.config.StripeProperties;
import io.allpad.payment.dto.SubscriptionDTO;
import io.allpad.payment.entity.Subscription;
import io.allpad.payment.error.SubscriptionException;
import io.allpad.payment.repository.SubscriptionRepository;
import io.allpad.payment.service.PaymentService;
import io.allpad.utils.ContextUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripePaymentServiceImpl implements PaymentService {
    private final StripeProperties stripeProperties;
    private final SubscriptionRepository subscriptionRepository;
    private final ContextUtils contextUtils;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeProperties.secretKey();
    }

    @Override
    public SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO) {
        try {
            var user = contextUtils.getUser();
            var customerId = findOrCreateCustomer(user);
            var existingSubscription = subscriptionRepository.findByUser(user);
            com.stripe.model.Subscription stripeSubscription;
            if (existingSubscription.isPresent()) {
                var subscription = existingSubscription.get();
                stripeSubscription = getSubscription(subscription.getSubscriptionId());
                var priceId = stripeSubscription.getItems().getData().getLast().getPrice().getId();
                if ("active".equals(stripeSubscription.getStatus()) && priceId.equals(subscription.getPriceId())) {
                    log.info("User already has the same active subscription");
                } else if ("incomplete".equals(stripeSubscription.getStatus())
                        && priceId.equals(subscription.getPriceId())) {
                    log.info("Retrying the payment subscription");
                } else if ("canceled".equals(stripeSubscription.getStatus())) {
                    stripeSubscription = createSubscription(customerId, subscriptionDTO);
                } else {
                    stripeSubscription = downgradeUpgradeSubscription(stripeSubscription, subscriptionDTO);
                }
            } else {
                stripeSubscription = createSubscription(customerId, subscriptionDTO);
            }
            var subscription = existingSubscription.orElse(new Subscription());
            subscription.setUser(user);
            subscription.setCustomerId(customerId);
            subscription.setSubscriptionId(stripeSubscription.getId());
            subscription.setPlanId(subscriptionDTO.planId());
            subscription.setPriceId(subscriptionDTO.priceId());
            subscription.setStatus(stripeSubscription.getStatus());
            subscription.setCurrentPeriodEnd(stripeSubscription.getItems().getData().getLast().getCurrentPeriodEnd());
            subscriptionRepository.save(subscription);
            var paymentIntentId = stripeSubscription.getLatestInvoiceObject().getPayments().getData().getLast().getPayment()
                    .getPaymentIntent();
            var paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return SubscriptionDTO.builder()
                    .planId(subscription.getPlanId())
                    .priceId(subscription.getPriceId())
                    .subscriptionId(subscription.getSubscriptionId())
                    .clientSecret(paymentIntent.getClientSecret())
                    .status(subscription.getStatus())
                    .build();
        } catch (StripeException e) {
            throw new SubscriptionException(String.format("Failed to create subscription: %s", e.getMessage()));
        }
    }

    private com.stripe.model.Subscription getSubscription(String subscriptionId) throws StripeException {
        var params = SubscriptionRetrieveParams.builder()
                .addAllExpand(List.of("latest_invoice.payments.data.payment"))
                .build();
        return com.stripe.model.Subscription.retrieve(subscriptionId, params, null);
    }

    private com.stripe.model.Subscription createSubscription(String customerId, SubscriptionDTO subscriptionDTO)
            throws StripeException {
        var params = SubscriptionCreateParams.builder()
                .setCustomer(customerId)
                .addItem(
                        SubscriptionCreateParams.Item.builder()
                                .setPrice(subscriptionDTO.priceId())
                                .build())
                .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
                .addAllExpand(List.of("latest_invoice.payments.data.payment"))
                .build();
        return com.stripe.model.Subscription.create(params);
    }

    private com.stripe.model.Subscription downgradeUpgradeSubscription(com.stripe.model.Subscription subscription,
                                                                       SubscriptionDTO subscriptionDTO) throws StripeException {
        var params = SubscriptionUpdateParams.builder()
                .addItem(
                        SubscriptionUpdateParams.Item.builder()
                                .setId(subscriptionDTO.subscriptionId())
                                .setPrice(subscriptionDTO.priceId())
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
            var subscription = subscriptionRepository.findByUser(user)
                    .orElseThrow(() -> new SubscriptionException("Subscription not found"));
            var stripeSubscription = com.stripe.model.Subscription.retrieve(subscription.getSubscriptionId());
            var params = SubscriptionCancelParams.builder().build();
            var updatedSubscription = stripeSubscription.cancel(params);
            subscription.setStatus(updatedSubscription.getStatus());
            subscriptionRepository.save(subscription);
        } catch (StripeException e) {
            throw new SubscriptionException(String.format("Failed to cancel subscription: %s", e.getMessage()));
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
                    handleSubscriptionUpdated((com.stripe.model.Subscription) stripeObject);
                    break;
                case "customer.subscription.deleted":
                    handleSubscriptionDeleted((com.stripe.model.Subscription) stripeObject);
                    break;
                default:
                    log.info("Unhandled event type: {}", event.getType());
            }
        } catch (Exception e) {
            throw new SubscriptionException(String.format("Webhook verification failed: %s", e.getMessage()));
        }
    }

    private void handlePaymentSucceeded(Invoice invoice) {
        log.info("Handling payment succeeded for invoice: {}, {}, {}",
                invoice.getId(), invoice.getCustomer(), invoice.getStatus());
        var subscriptionOpt = subscriptionRepository.findByCustomerId(invoice.getCustomer());
        if (subscriptionOpt.isPresent()) {
            var subscription = subscriptionOpt.get();
            subscription.setInvoiceStatus(invoice.getStatus());
            subscriptionRepository.save(subscription);
        }
    }

    private void handleSubscriptionUpdated(com.stripe.model.Subscription stripeSubscription) {
        log.info("Handling subscription updated for subscription: {}, {}, {}",
                stripeSubscription.getId(), stripeSubscription.getCustomer(), stripeSubscription.getStatus());
        var subscriptionOpt = subscriptionRepository.findBySubscriptionId(stripeSubscription.getId());
        if (subscriptionOpt.isPresent()) {
            var subscription = subscriptionOpt.get();
            subscription.setStatus(stripeSubscription.getStatus());
            subscription.setCurrentPeriodEnd(stripeSubscription.getItems().getData().getLast().getCurrentPeriodEnd());
            subscriptionRepository.save(subscription);
        }
    }

    private void handleSubscriptionDeleted(com.stripe.model.Subscription stripeSubscription) {
        log.info("Handling subscription deleted for subscription: {}, {}, {}",
                stripeSubscription.getId(), stripeSubscription.getCustomer(), stripeSubscription.getStatus());
        var subscriptionOpt = subscriptionRepository.findBySubscriptionId(stripeSubscription.getId());
        if (subscriptionOpt.isPresent()) {
            var subscription = subscriptionOpt.get();
            subscription.setStatus(stripeSubscription.getStatus());
            subscription.setCurrentPeriodEnd(stripeSubscription.getItems().getData().getLast().getCurrentPeriodEnd());
            subscriptionRepository.save(subscription);
        }
    }

    private String findOrCreateCustomer(User user) throws StripeException {
        var existingSub = subscriptionRepository.findByUser(user);
        if (existingSub.isPresent() && existingSub.get().getCustomerId() != null) {
            return existingSub.get().getCustomerId();
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
