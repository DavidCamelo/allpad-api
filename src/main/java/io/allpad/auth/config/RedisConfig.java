package io.allpad.auth.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final JsonMapper jsonMapper;

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "allpad::" + cacheName + "::");
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        JsonMapper mapperWithType = jsonMapper.rebuild()
                .activateDefaultTyping(
                        BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build(),
                        DefaultTyping.NON_FINAL_AND_RECORDS,
                        JsonTypeInfo.As.PROPERTY
                )
                .build();

        return (builder) -> builder
                .withCacheConfiguration("dto::users",
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofDays(1))
                                .disableCachingNullValues()
                                .computePrefixWith(cacheName -> "allpad::" + cacheName + "::")
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                        new GenericJacksonJsonRedisSerializer(mapperWithType))
                                ).serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                                        new StringRedisSerializer())
                                ))
                .withCacheConfiguration("reports",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)));
    }
}
