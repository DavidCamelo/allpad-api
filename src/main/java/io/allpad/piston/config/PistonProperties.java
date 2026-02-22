package io.allpad.piston.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "piston")
public record PistonProperties(
        String url,
        String user,
        String password) {
}
