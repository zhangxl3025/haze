package com.zxl.haze.redis;

import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Data
public class RedisLock {


    private String lockKey;
    private String requestId;
    private int expireTime = Integer.MAX_VALUE;
    private int waitTimeout = 2000;

    public RedisLock() {

    }

    public RedisLock(String lockKey, String requestId) {
        this.lockKey = lockKey;
        this.requestId = requestId;
    }


    public RedisLock(String lockKey, String requestId, int expireTime) {
        this.lockKey = lockKey;
        this.requestId = requestId;
        this.expireTime = expireTime;
    }

    public RedisLock(String lockKey, String requestId, int expireTime, int waitTimeout) {
        this.lockKey = lockKey;
        this.requestId = requestId;
        this.expireTime = expireTime;
        this.waitTimeout = waitTimeout;
    }

    private static final Long SUCCESS = 1L;

    private static RedisTemplate<Object, Object> redisTemplate;

    @Resource
    public void setRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {
        RedisLock.redisTemplate = redisTemplate;
    }

    public static RedisTemplate<Object, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void runnableLock(Runnable runnable) {
        boolean lock = tryLock(lockKey, requestId, expireTime, waitTimeout);
        if (!lock) {
            System.out.println("尝试获取分布式锁-key[{}]异常" + lockKey);
        }
        try {
            runnable.run();
        } finally {
            releaseLock(lockKey, requestId);
        }
    }

    public <T> T supplierLock(Supplier<T> supplier) {
        boolean lock = tryLock(lockKey, requestId, expireTime, waitTimeout);
        if (!lock) {
            System.out.println("尝试获取分布式锁-key[{}]异常" + lockKey);
        }
        try {
            return supplier.get();
        } finally {
            releaseLock(lockKey, requestId);
        }

    }

    /**
     * 获取分布式锁
     *
     * @param lockKey     锁
     * @param requestId   请求标识
     * @param expireTime  单位秒    锁过期时间
     * @param waitTimeout 单位毫秒  获取锁超时时间
     * @return 是否获取成功
     */
    public static boolean tryLock(String lockKey, String requestId, int expireTime, long waitTimeout) {
        // 当前时间
        long nanoTime = System.nanoTime();
        try {
            String script = "if redis.call('setNx',KEYS[1],ARGV[1]) then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end end";
            int count = 0;
            do {
                RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
                Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId, expireTime);
                if (SUCCESS.equals(result)) {
                    return true;
                }
                //休眠500毫秒
                Thread.sleep(500L);
                count++;
            } while ((System.nanoTime() - nanoTime) < TimeUnit.MILLISECONDS.toNanos(waitTimeout));

        } catch (Exception e) {
            System.out.println("尝试获取分布式锁-key[{}]异常" + lockKey);
        }
        return false;
    }

    public boolean tryLock() {
        return tryLock(lockKey, requestId, expireTime, waitTimeout);
    }

    /**
     * 释放锁
     *
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseLock(String lockKey, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
        return SUCCESS.equals(result);
    }

    public boolean releaseLock() {
        return releaseLock(lockKey, requestId);
    }
}
