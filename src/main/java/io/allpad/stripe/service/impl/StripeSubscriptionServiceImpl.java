package io.allpad.stripe.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.SubscriptionCancelParams;
import com.stripe.param.SubscriptionCreateParams;
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
            if (existingSubscription.isPresent() && "active".equals(existingSubscription.get().getStatus())) {
                throw new StripeSubscriptionException("User already has an active subscription");
            }
            var params = SubscriptionCreateParams.builder()
                    .setCustomer(customerId)
                    .addItem(
                            SubscriptionCreateParams.Item.builder()
                                    .setPrice(stripeSubscriptionDTO.planId())
                                    .build())
                    .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
                    .addAllExpand(List.of("latest_invoice.payment_intent"))
                    .build();
            var stripeSubscription = Subscription.create(params);
            var subscription = existingSubscription.orElse(new StripeSubscription());
            subscription.setUser(user);
            subscription.setStripeCustomerId(customerId);
            subscription.setStripeSubscriptionId(stripeSubscription.getId());
            subscription.setPlanId(stripeSubscriptionDTO.planId());
            subscription.setStatus(stripeSubscription.getStatus());
            subscription.setCurrentPeriodEnd(stripeSubscription.getEndedAt());
            stripeSubscriptionRepository.save(subscription);
            var clientSecret = "";
            if (stripeSubscription.getLatestInvoiceObject() != null
                    && stripeSubscription.getLatestInvoiceObject().getConfirmationSecret() != null
                    && stripeSubscription.getLatestInvoiceObject().getConfirmationSecret().getClientSecret() != null) {
                clientSecret = stripeSubscription.getLatestInvoiceObject().getConfirmationSecret().getClientSecret();
            }
            return StripeSubscriptionDTO.builder()
                    .subscriptionId(subscription.getPlanId())
                    .clientSecret(clientSecret)
                    .subscriptionId(subscription.getStripeSubscriptionId())
                    .status(subscription.getStatus())
                    .build();
        } catch (StripeException e) {
            throw new StripeSubscriptionException(String.format("Failed to create subscription: %s", e.getMessage()));
        }
    }

    @Override
    public void cancelSubscription() {
        try {
            var user = contextUtils.getUser();
            var subscription = stripeSubscriptionRepository.findByUser(user)
                    .orElseThrow(() -> new StripeSubscriptionException("Subscription not found"));
            var stripeSubscription = Subscription.retrieve(subscription.getStripeSubscriptionId());
            var params = SubscriptionCancelParams.builder().build();
            var updatedSubscription = stripeSubscription.cancel(params);
            subscription.setStatus(updatedSubscription.getStatus());
            stripeSubscriptionRepository.save(subscription);
        } catch (StripeException e) {
            throw new StripeSubscriptionException(String.format("Failed to cancel subscription: %s", e.getMessage()));
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
            throw new StripeSubscriptionException(String.format("Webhook verification failed: %s", e.getMessage()));
        }
    }

    private void handlePaymentSucceeded(Invoice invoice) {
        if (invoice.getId() == null) {
            return;
        }
        var subscriptionOpt = stripeSubscriptionRepository.findByStripeSubscriptionId(invoice.getId());
        if (subscriptionOpt.isPresent()) {
            var stripeSubscription = subscriptionOpt.get();
            stripeSubscription.setStatus("active");
            // Optionally update period end from the invoice or fetch subscription
            stripeSubscriptionRepository.save(stripeSubscription);
        }
    }

    private void handleSubscriptionUpdated(Subscription stripeSubscription) {
        var subscriptionOpt = stripeSubscriptionRepository.findByStripeSubscriptionId(stripeSubscription.getId());
        if (subscriptionOpt.isPresent()) {
            var subscription = subscriptionOpt.get();
            subscription.setStatus(stripeSubscription.getStatus());
            subscription.setCurrentPeriodEnd(stripeSubscription.getEndedAt());
            subscription.setPlanId(stripeSubscription.getItems().getData().getFirst().getPrice().getId());
            stripeSubscriptionRepository.save(subscription);
        }
    }

    private void handleSubscriptionDeleted(Subscription stripeSubscription) {
        var subscriptionOpt = stripeSubscriptionRepository.findByStripeSubscriptionId(stripeSubscription.getId());
        if (subscriptionOpt.isPresent()) {
            StripeSubscription subscription = subscriptionOpt.get();
            subscription.setStatus("canceled");
            stripeSubscriptionRepository.save(subscription);
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
            return customers.getData().getFirst().getId();
        }
        var createParams = CustomerCreateParams.builder()
                .setEmail(user.getEmail())
                .setName(user.getName() + " " + user.getLastName())
                .build();
        var customer = Customer.create(createParams);
        return customer.getId();
    }
}
