package io.allpad.piston.config;

import io.allpad.piston.utils.http.PistonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class HttpClientConfig {

    @Bean
    public PistonClient userClient(PistonProperties pistonProperties) {
        var auth = pistonProperties.user() + ":" + pistonProperties.password();
        var restClient = RestClient.builder()
                .baseUrl(pistonProperties.url())
                .defaultHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8)))
                .build();
        var factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
        return factory.createClient(PistonClient.class);
    }
}

