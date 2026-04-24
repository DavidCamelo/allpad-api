package io.allpad.auth.service.impl;

import io.allpad.auth.repository.UserRepository;
import io.allpad.auth.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {
    private final CacheManager cacheManager;
    private final UserRepository userRepository;

    @Override
    public void evictCacheObjects(String cacheName) {
        var redisCache = getRedisCache(cacheName);
        var users = userRepository.findAllByEmailIsNotNullAndUsernameIsNotNull();
        for (var user : users) {
            redisCache.evict(user.email());
            redisCache.evict(user.username());
        }
    }

    @Override
    public List<Object> getCacheObjects(String cacheName) {
        var allData = new ArrayList<>();
        var redisCache = getRedisCache(cacheName);
        var users = userRepository.findAllByEmailIsNotNullAndUsernameIsNotNull();
        for (var user : users) {
            var value = redisCache.get(user.email());
            if (value != null) {
                allData.add(value.get());
            }
            value = redisCache.get(user.username());
            if (value != null) {
                allData.add(value.get());
            }
        }
        return allData;
    }

    private RedisCache getRedisCache(String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache instanceof RedisCache redisCache) {
            return redisCache;
        }
        throw new RuntimeException("Cache not found");
    }
}
