package com.zxl.haze.redis.cache;

import java.util.Set;
@FunctionalInterface
public interface CacheConfigs {

    Set<CacheConfig> cacheConfigSet();
}
