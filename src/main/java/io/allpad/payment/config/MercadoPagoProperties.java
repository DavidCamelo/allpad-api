package io.allpad.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mercadopago")
public record MercadoPagoProperties(
                String accessToken,
                String publicKey,
                String webhookSecret) {
}