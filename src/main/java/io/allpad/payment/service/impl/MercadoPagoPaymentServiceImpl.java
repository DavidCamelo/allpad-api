package io.allpad.payment.service.impl;

import io.allpad.payment.config.MercadoPagoProperties;
import io.allpad.payment.dto.SubscriptionDTO;
import io.allpad.payment.repository.SubscriptionRepository;
import io.allpad.payment.service.PaymentService;
import io.allpad.utils.ContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MercadoPagoPaymentServiceImpl implements PaymentService {
    private final MercadoPagoProperties mercadopagoProperties;
    private final SubscriptionRepository subscriptionRepository;
    private final ContextUtils contextUtils;

    @Override
    public SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO) {
        var user = contextUtils.getUser();
        log.info("Creating Mercado Pago subscription for user: {}", user.getEmail());
        // Implementation depends on Mercado Pago SDK integration
        return SubscriptionDTO.builder()
                .planId(subscriptionDTO.planId())
                .priceId(subscriptionDTO.priceId())
                .status("pending")
                .build();
    }

    @Override
    public void cancelSubscription() {
        var user = contextUtils.getUser();
        log.info("Canceling Mercado Pago subscription for user: {}", user.getEmail());
        subscriptionRepository.findByUser(user).ifPresent(subscription -> {
            subscription.setStatus("cancelled");
            subscriptionRepository.save(subscription);
        });
    }

    @Override
    public void handleWebhook(String payload, String sigHeader) {
        log.info("Handling Mercado Pago webhook request");
        // Implementation for Mercado Pago IPN/Webhook verification goes here
    }
}
