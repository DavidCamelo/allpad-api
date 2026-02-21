package io.allpad.stripe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "piston")
public record PistonProperties(
        String user,
        String password) {
}
