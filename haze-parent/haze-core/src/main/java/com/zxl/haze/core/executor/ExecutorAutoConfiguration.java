package com.zxl.haze.core.executor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;




@Slf4j
@Configuration
@ComponentScan(basePackages = {"com.zxl.haze.core.executor"})
public class ExecutorAutoConfiguration {


    @Resource(name="defaultExecutorConfigurer")
    private DefaultExecutorConfigurer defaultExecutorConfigurer;



    @Primary
    @Bean
    public ThreadPoolTaskExecutor defaultTaskExecutor() {
        return defaultExecutorConfigurer.createTaskExecutor();
    }

    @Primary
    @Bean
    public ThreadPoolExecutor defaultExecutor() {
        return defaultTaskExecutor().getThreadPoolExecutor();
    }



    @Bean
    AsyncConfigurer asyncConfigurer() {
        return new AsyncConfigurer() {
            @Override
            public Executor getAsyncExecutor() {
                return defaultTaskExecutor();
            }
            @Override
            public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
                return (throwable, method, params) -> log.error("async execute error, method={}, params={}", method.getName(), Arrays.toString(params), throwable);
            }
        };
    }
}
