package com.zxl.haze.task.executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;

@Configuration
public class ExportTaskExecutorConfig {

    @Resource(name = "taskInfoExecutorConfiguration")
    TaskInfoExecutorConfiguration taskInfoExecutorConfiguration;

    @Bean("taskInfoThreadPoolTaskExecutor")
    ThreadPoolTaskExecutor taskInfoThreadPoolTaskExecutor() {
        return taskInfoExecutorConfiguration.createTaskExecutor();
    }
}
