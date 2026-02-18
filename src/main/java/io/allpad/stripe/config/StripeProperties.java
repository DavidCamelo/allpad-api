package io.allpad.stripe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "stripe")
public record StripeProperties(
        String secretKey,
        String publicKey,
        String webhookSecret) {
}
