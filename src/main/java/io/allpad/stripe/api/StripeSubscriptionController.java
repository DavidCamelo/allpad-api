package io.allpad.stripe.api;

import lombok.RequiredArgsConstructor;
import io.allpad.stripe.dto.StripeSubscriptionDTO;
import io.allpad.stripe.service.StripeSubscriptionService;
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
@RequestMapping(value = "api/{version}/stripe", version = "v1")
@RequiredArgsConstructor
public class StripeSubscriptionController {
    private final StripeSubscriptionService stripeSubscriptionService;

    @Operation(summary = "Create subscription", description = "Create subscription")
    @PostMapping("subscription")
    public ResponseEntity<StripeSubscriptionDTO> createSubscription(@RequestBody StripeSubscriptionDTO stripeSubscriptionDTO) {
        return ResponseEntity.ok(stripeSubscriptionService.createSubscription(stripeSubscriptionDTO));
    }

    @Operation(summary = "Cancel subscription", description = "Cancel subscription")
    @DeleteMapping("subscription")
    public ResponseEntity<Void> cancelSubscription() {
        stripeSubscriptionService.cancelSubscription();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Stripe Webhook", description = "Handle Stripe Webhook events")
    @PostMapping("webhook")
    public void handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        stripeSubscriptionService.handleWebhook(payload, sigHeader);
    }
}
