package io.allpad.cache.service;

import java.util.List;

public interface CacheService {
    void evictCacheObjects(String cacheName);

    List<Object> getCacheObjects(String cacheName);
}
