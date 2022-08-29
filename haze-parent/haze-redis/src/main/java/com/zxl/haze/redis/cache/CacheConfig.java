package com.zxl.haze.redis.cache;

import lombok.Data;

import java.time.temporal.ChronoUnit;
import java.util.Random;

@Data
public class CacheConfig {

    public final String name;
    public final int ttl;
    public final ChronoUnit chronoUnit;

    public CacheConfig(String name, int ttl, ChronoUnit chronoUnit) {
        this.name = name;
        this.ttl = ttl + new Random().nextInt(ttl);
        this.chronoUnit = chronoUnit;
    }

    public String getName() {
        return name;
    }

    public int getTtl() {
        return ttl;
    }

    public ChronoUnit getChronoUnit() {
        return chronoUnit;
    }
}
