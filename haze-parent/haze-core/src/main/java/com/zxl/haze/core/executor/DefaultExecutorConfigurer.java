package com.zxl.haze.core.executor;



import com.zxl.haze.core.executor.support.TaskContextUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Data
@Configuration("defaultExecutorConfigurer")
@ConfigurationProperties(prefix = "default-executor")
@RefreshScope
public class DefaultExecutorConfigurer implements Serializable{

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    protected Integer corePoolSize = CPU_COUNT;
    protected Integer maxPoolSize = CPU_COUNT * 2 + 1;
    protected Integer queueCapacity = 4000;
    protected String threadNamePrefix = "default-executor-";
    protected Integer keepAliveSeconds = 10;
    protected boolean allowCoreThreadTimeOut = false;
    protected boolean waitForTasksToCompleteOnShutdown = false;
    protected int awaitTerminationSeconds = 10;


    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setQueueCapacity(Integer queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    protected RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy() {
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RejectedExecutionException("任务池已满,请稍后再试");
        }
    };


    protected TaskDecorator taskDecorator(ThreadPoolTaskExecutor taskExecutor){
        return r -> {
            Map<String, Object> contextMap = TaskContextUtil.replay();
            return () -> {
                try {
                    TaskContextUtil.restore(contextMap);
                    showThreadPoolInfo("task-begin, ", taskExecutor);
                    r.run();
                }finally {
                    showThreadPoolInfo("task-end, ", taskExecutor);
                    TaskContextUtil.clear();
                }
            };
        };
    }


    public ThreadPoolTaskExecutor createTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(corePoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        taskExecutor.setThreadNamePrefix(threadNamePrefix);
        taskExecutor.setKeepAliveSeconds(keepAliveSeconds);
        taskExecutor.setAllowCoreThreadTimeOut(allowCoreThreadTimeOut);
        taskExecutor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        taskExecutor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        taskExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
        taskExecutor.setTaskDecorator(taskDecorator(taskExecutor));
        taskExecutor.initialize();
        return taskExecutor;
    }

    public static void showThreadPoolInfo(String desc, ThreadPoolTaskExecutor taskExecutor) {
        log.info(desc + "corePoolSize [{}], maxPoolSize [{}]", taskExecutor.getCorePoolSize(), taskExecutor.getMaxPoolSize());
        log.info(desc + "taskCount [{}], completedTaskCount [{}], activeCount [{}], queueSize [{}]",
                taskExecutor.getThreadPoolExecutor().getTaskCount(),
                taskExecutor.getThreadPoolExecutor().getCompletedTaskCount(),
                taskExecutor.getThreadPoolExecutor().getActiveCount(),
                taskExecutor.getThreadPoolExecutor().getQueue().size());
    }


}
