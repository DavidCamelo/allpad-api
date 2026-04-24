package io.allpad.auth.api;

import io.allpad.auth.service.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Cache API")
@RestController
            @RequestMapping(value = "api/{version}/cache", version = "v1")
@RequiredArgsConstructor
public class CacheController {
    private final CacheService cacheService;

    @Operation(summary = "Get all", description = "Get all users")
    @GetMapping("{cacheName}/evict")
    public ResponseEntity<Void> evictCacheObjects(@PathVariable String cacheName) {
        cacheService.evictCacheObjects(cacheName);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all", description = "Get all users")
    @GetMapping("{cacheName}")
    public ResponseEntity<List<Object>> getCacheObjects(@PathVariable String cacheName) {
        return ResponseEntity.ok(cacheService.getCacheObjects(cacheName));
    }
}
