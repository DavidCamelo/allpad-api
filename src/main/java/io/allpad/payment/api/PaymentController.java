package io.allpad.payment.api;

import lombok.RequiredArgsConstructor;
import io.allpad.payment.dto.SubscriptionDTO;
import io.allpad.payment.service.impl.MercadoPagoPaymentServiceImpl;
import io.allpad.payment.service.impl.StripePaymentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Stripe API")
@RestController
@RequestMapping(value = "api/{version}/payments", version = "v1")
@RequiredArgsConstructor
public class PaymentController {
    private final StripePaymentServiceImpl stripePaymentService;
    private final MercadoPagoPaymentServiceImpl mercadoPagoPaymentService;

    @Operation(summary = "Create subscription", description = "Create subscription")
    @PostMapping("subscription")
    public ResponseEntity<SubscriptionDTO> createSubscription(@RequestBody SubscriptionDTO subscriptionDTO) {
        return ResponseEntity.ok(stripePaymentService.createSubscription(subscriptionDTO));
    }

    @Operation(summary = "Cancel subscription", description = "Cancel subscription")
    @DeleteMapping("subscription")
    public ResponseEntity<Void> cancelSubscription() {
        stripePaymentService.cancelSubscription();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Stripe Webhook", description = "Handle Stripe Webhook events")
    @PostMapping("webhook")
    public void handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        stripePaymentService.handleWebhook(payload, sigHeader);
    }
}
