package io.allpad.cache.service.impl;

import io.allpad.auth.repository.UserRepository;
import io.allpad.cache.error.CacheException;
import io.allpad.cache.service.CacheService;
import io.allpad.pad.repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {
    private final CacheManager cacheManager;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;

    @Override
    public void evictCacheObjects(String cacheName) {
        switch (cacheName) {
            case "dto::user" -> evictUserCache(getRedisCache(cacheName));
            case "dto::history" -> evictHistoryCache(getRedisCache(cacheName));
            default -> log.error("Cache not found");
        }
    }

    private void evictUserCache(RedisCache redisCache) {
        var users = userRepository.findAllByEmailIsNotNullAndUsernameIsNotNull();
        for (var user : users) {
            redisCache.evict(user.email());
            redisCache.evict(user.username());
        }
    }

    private void evictHistoryCache(RedisCache redisCache) {
        var histories = historyRepository.findAllByIdIsNotNull();
        for (var history : histories) {
            redisCache.evict(history.id());
        }
    }

    @Override
    public List<Object> getCacheObjects(String cacheName) {
        return switch (cacheName) {
            case "dto::user" -> getUsersCache(getRedisCache(cacheName));
            case "dto::history" -> getHistoryCache(getRedisCache(cacheName));
            default -> List.of();
        };
    }

    private List<Object> getUsersCache(RedisCache redisCache) {
        try {
            var allData = new ArrayList<>();
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
        } catch (Exception e) {
            throw new CacheException(e.getMessage());
        }
    }

    private List<Object> getHistoryCache(RedisCache redisCache) {
        try {
            var allData = new ArrayList<>();
            var histories = historyRepository.findAllByIdIsNotNull();
            for (var history : histories) {
                var value = redisCache.get(history.id());
                if (value != null) {
                    allData.add(value.get());
                }
            }
            return allData;
        } catch (Exception e) {
            throw new CacheException(e.getMessage());
        }
    }

    private RedisCache getRedisCache(String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache instanceof RedisCache redisCache) {
            return redisCache;
        }
        throw new CacheException("Cache not found");
    }
}
