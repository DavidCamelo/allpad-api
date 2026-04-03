package io.allpad.payment.service.impl;

import org.springframework.stereotype.Service;

import io.allpad.payment.dto.SubscriptionDTO;
import io.allpad.payment.repository.SubscriptionRepository;
import io.allpad.payment.service.PaymentService;
import io.allpad.utils.ContextUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final StripeSubscriptionServiceImpl stripeSubscriptionService;
    private final MercadoPagoSubscriptionServiceImpl mercadoPagoSubscriptionService;
    private final SubscriptionRepository subscriptionRepository;
    private final ContextUtils contextUtils;

    @Override
    public SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO) {
        if ("mercadopago".equalsIgnoreCase(subscriptionDTO.provider())) {
            return mercadoPagoSubscriptionService.createSubscription(subscriptionDTO);
        }
        return stripeSubscriptionService.createSubscription(subscriptionDTO);
    }

    @Override
    public void cancelSubscription() {
        subscriptionRepository.findByUser(contextUtils.getUser()).ifPresent(sub -> {
            if (sub.getSubscriptionId() != null && !sub.getSubscriptionId().startsWith("sub_")) {
                mercadoPagoSubscriptionService.cancelSubscription();
            } else {
                stripeSubscriptionService.cancelSubscription();
            }
        });
    }

    @Override
    public void handleWebhook(String payload, String stripeSignature, String mercadoPagoSignature) {
        if (stripeSignature != null) {
            stripeSubscriptionService.handleWebhook(payload, stripeSignature);
        } else if (mercadoPagoSignature != null) {
            mercadoPagoSubscriptionService.handleWebhook(payload, mercadoPagoSignature);
        }
    }

}
