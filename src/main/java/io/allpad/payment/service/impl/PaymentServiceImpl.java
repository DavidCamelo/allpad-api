package io.allpad.payment.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import io.allpad.payment.dto.SubscriptionDTO;
import io.allpad.payment.repository.SubscriptionRepository;
import io.allpad.payment.service.PaymentService;
import io.allpad.payment.service.SubscriptionService;
import io.allpad.utils.ContextUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final List<SubscriptionService> subscriptionServices;
    private final SubscriptionRepository subscriptionRepository;
    private final ContextUtils contextUtils;

    @Override
    public SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO) {
        return subscriptionServices.stream()
                .filter(s -> s.canHandle(subscriptionDTO.provider()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No subscription service found for provider: " + subscriptionDTO.provider()))
                .createSubscription(subscriptionDTO);
    }

    @Override
    public void cancelSubscription() {
        subscriptionRepository.findByUser(contextUtils.getUser()).ifPresent(sub -> subscriptionServices.stream()
                .filter(s -> s.canHandle(sub.getProvider()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No subscription service found for provider: " + sub.getProvider()))
                .cancelSubscription());
    }

    @Override
    public void handleWebhook(String payload, String stripeSignature, String mercadoPagoSignature) {
        if (stripeSignature != null) {
            subscriptionServices.stream()
                    .filter(s -> s.canHandle("stripe"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No subscription service found for provider: stripe"))
                    .handleWebhook(payload, stripeSignature);
        } else if (mercadoPagoSignature != null) {
            subscriptionServices.stream()
                    .filter(s -> s.canHandle("mercadopago"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No subscription service found for provider: mercadopago"))
                    .handleWebhook(payload, mercadoPagoSignature);
        }
    }

}
