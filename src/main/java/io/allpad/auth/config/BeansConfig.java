package io.allpad.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class BeansConfig {

    @Bean
    public JsonMapper jsonMapper() {
        return new JsonMapper();
    }
}
