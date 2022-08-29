package com.zxl.haze.task.executor;



import com.zxl.haze.core.executor.DefaultExecutorConfigurer;
import com.zxl.haze.core.executor.support.TaskContextUtil;
import com.zxl.haze.task.dao.TaskDao;
import com.zxl.haze.task.domain.TaskInfo;
import com.zxl.haze.task.manager.TaskManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
@Slf4j
@Configuration("taskInfoExecutorConfiguration")
@ConfigurationProperties(prefix = "export-task-info-executor")
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskInfoExecutorConfiguration extends DefaultExecutorConfigurer {

    protected String threadNamePrefix = "task-info-executor-";

    @Resource
    TaskDao taskDao;

    RejectedExecutionHandler rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy() {
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RejectedExecutionException("task-info-executor 任务池已满,请稍后再试");
        }
    };

    @Override
    protected TaskDecorator taskDecorator(ThreadPoolTaskExecutor exportTaskExecutor) {
        return r -> {
            Map<String, Object> contextMap = TaskContextUtil.replay();
            return () -> {
                try {
                    TaskContextUtil.restore(contextMap);
                    String taskId = TaskManager.getTaskId();
                    if (taskId != null){
                        TaskManager.taskThreadMap.put(taskId,Thread.currentThread().getId());
                        TaskInfo taskInfo = taskDao.selectById(taskId);
                        if (null == taskInfo){
                            log.info("task id:{} check status, been cancelled", taskId);
                            return;
                        }
                        TaskInfo updateTaskInfo= new TaskInfo();
                        updateTaskInfo.setId(taskId);
                        updateTaskInfo.setTaskStatus(TaskInfo.TaskStatus.EXECUTING.getType());
                        taskDao.update(updateTaskInfo);
                    }
                    showThreadPoolInfo("task-begin, ", exportTaskExecutor);
                    r.run();
                } finally {
                    try {
                        try {
                            showThreadPoolInfo("task-end, ", exportTaskExecutor);
                        }finally {
                            try {
                                String taskId = TaskManager.getTaskId();
                                TaskManager.taskThreadMap.remove(taskId);
                            }finally {
                                TaskManager.threadIsTerminatedMap.remove(Thread.currentThread().getId());
                            }
                        }
                    }finally {
                        TaskContextUtil.clear();
                    }
                }
            };
        };
    }
}
