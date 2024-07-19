package ru.utlc.referencedataservice.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.utlc.referencedataservice.constants.CacheNames;

@Configuration
@EnableCaching
public class CacheConfig {
    public CacheConfig() {
    }

    @Bean
    public ConcurrentMapCacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CacheNames.COUNTRIES);
    }
}
