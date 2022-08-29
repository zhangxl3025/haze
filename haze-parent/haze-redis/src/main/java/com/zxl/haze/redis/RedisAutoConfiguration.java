package com.zxl.haze.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.zxl.haze.core.context.AppContext;
import com.zxl.haze.redis.cache.CacheConfig;
import com.zxl.haze.redis.cache.CacheConfigs;
import com.zxl.haze.redis.cache.CacheEnum;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class RedisAutoConfiguration {


    @Resource
    private CacheConfigs cacheConfigs;


    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public RedisLock redisLock() {
        return new RedisLock();
    }


    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate<String, Object> redisTemplate() {
        //设置序列化工具
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(stringSerializer()); // key序列化
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer()); // value序列化
        redisTemplate.setHashKeySerializer(stringSerializer()); // Hash key序列化
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer()); // Hash value序列化
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


    @Bean
    @ConditionalOnMissingBean
    public CacheConfigs caches() {
        return () -> Arrays.stream(CacheEnum.values())
                .map(RedisAutoConfiguration::fromCacheEnum)
                .collect(Collectors.toSet());
    }

    static CacheConfig fromCacheEnum(CacheEnum cacheEnum) {
        return new CacheConfig(cacheEnum.name, cacheEnum.ttl, cacheEnum.chronoUnit);
    }


    @Bean
    public CacheManager cacheManager() {
        Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
        for (CacheConfig value : cacheConfigs.cacheConfigSet()) {
            RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().computePrefixWith(cacheName -> AppContext.APP_NAME + ":" + cacheName + ":").entryTtl(Duration.of(value.getTtl(), value.getChronoUnit()));
            setSerializer(redisCacheConfiguration);
            configurationMap.put(value.getName(), redisCacheConfiguration);
        }
        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .initialCacheNames(configurationMap.keySet())
                .withInitialCacheConfigurations(configurationMap)
                .build();
    }

    private void setSerializer(RedisCacheConfiguration redisCacheConfiguration) {
        redisCacheConfiguration.serializeKeysWith(RedisSerializationContext
                .SerializationPair
                .fromSerializer(stringSerializer()));
        redisCacheConfiguration.serializeValuesWith(RedisSerializationContext
                .SerializationPair
                .fromSerializer(jackson2JsonRedisSerializer()));
    }


    private Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //非final类型的对象，把对象类型也序列化进去，以便反序列化推测正确的类型
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        //null字段不显示
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //POJO无public属性或方法时不报错
        om.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }


    private RedisSerializer<String> stringSerializer() {
        return new StringRedisSerializer();
    }


    @Bean
    @Primary
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            String sb = method.getClass().getSimpleName() +
                    ":" +
                    method.getName() +
                    ":" +
                    StringUtils.arrayToDelimitedString(params, "_");
            return sb;
        };
    }
}
