package com.zxl.haze.core.executor;

import com.google.common.collect.Maps;
import com.zxl.haze.core.executor.support.ActionConfigEvent;
import com.zxl.haze.core.executor.support.PropertyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

/**
 * 支持线程池参数动态修改
 */
@Slf4j
@Component
public class ExecutorListener implements ApplicationListener<ActionConfigEvent> {


    @Resource
    ThreadPoolTaskExecutor defaultThreadPoolTaskExecutor;


    public static final String DEFAULT_EXECUTOR_CORE_POOL_SIZE = "default-executor.core-pool-size";
    public static final String DEFAULT_EXECUTOR_MAX_POOL_SIZE = "default-executor.max-pool-size";
    public static final String DEFAULT_EXECUTOR_KEEP_ALIVE_SECONDS = "default-executor.keep-alive-seconds";
    public static final String DEFAULT_EXECUTOR_KEEP_QUEUE_CAPACITY = "default-executor.queue-capacity";


    @Override
    public void onApplicationEvent(ActionConfigEvent event) {
        log.info(event.getPropertyMap().toString());
        Map<String, String> afterValueMap = Maps.newHashMap();
        for (Map.Entry<String, Map<String, String>> stringHashMapEntry : event.getPropertyMap().entrySet()) {
            Map<String, String> beforeAfterValueMap = stringHashMapEntry.getValue();
            afterValueMap.put(stringHashMapEntry.getKey(), beforeAfterValueMap.get(PropertyUtil.AFTER));
        }


        getInteger(DEFAULT_EXECUTOR_CORE_POOL_SIZE, afterValueMap).ifPresent(val -> defaultThreadPoolTaskExecutor.setCorePoolSize(val));
        getInteger(DEFAULT_EXECUTOR_MAX_POOL_SIZE, afterValueMap).ifPresent(val -> defaultThreadPoolTaskExecutor.setMaxPoolSize(val));
        getInteger(DEFAULT_EXECUTOR_KEEP_ALIVE_SECONDS, afterValueMap).ifPresent(val -> defaultThreadPoolTaskExecutor.setKeepAliveSeconds(val));
        getInteger(DEFAULT_EXECUTOR_KEEP_QUEUE_CAPACITY, afterValueMap).ifPresent(val -> defaultThreadPoolTaskExecutor.setQueueCapacity(val));

    }

    private static Optional<Integer> getInteger(String key, Map<String, String> afterValueMap) {
        if (!afterValueMap.containsKey(key) || afterValueMap.get(key) == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(afterValueMap.get(key)));
        } catch (Exception e) {
            log.error("配置项={}配置错误", key);
            return Optional.empty();
        }
    }


}