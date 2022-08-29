package com.zxl.haze.task.manager;


import com.alibaba.fastjson.JSON;
import com.zxl.haze.task.dao.TaskDao;
import com.zxl.haze.task.domain.InitTaskReq;
import com.zxl.haze.task.domain.TaskInfo;
import com.zxl.haze.task.topic.TaskTopic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Component
@Slf4j
@EnableBinding({TaskTopic.class})
public class TaskManager {
    @Resource
    TaskDao taskDao;
    @Resource(name = "taskInfoThreadPoolTaskExecutor")
    ThreadPoolTaskExecutor taskInfoThreadPoolTaskExecutor;

    public static Map<String, Long> taskThreadMap = new HashMap<>();
    public static Map<Long, Boolean> threadIsTerminatedMap = new ConcurrentHashMap<>();

    private static ThreadLocal<String> taskIdContext = new ThreadLocal<>();

    @Resource(name = TaskTopic.OUTPUT)
    private MessageChannel messageChannel;

    public static String getTaskId() {
        return taskIdContext.get();
    }

    public static void setTaskId(String taskId) {
        taskIdContext.set(taskId);
    }

    public static void removeTaskId() {
        taskIdContext.remove();
    }

    @Transactional
    public void addTask(Supplier<Object> supplier, InitTaskReq initTaskReq) {
        addTask(supplier, initTaskReq, taskInfoThreadPoolTaskExecutor);
    }

    @Transactional
    public void addTask(Supplier<Object> supplier, InitTaskReq initTaskReq, ThreadPoolTaskExecutor taskExecutor) {
        String taskId = initTaskReq.getTaskId();
        if (taskId == null) {
            taskId = initTask(initTaskReq);
        }
        TaskManager.setTaskId(taskId);
        String finalId = taskId;
        TransactionSynchronizationAdapter transactionSynchronizationAdapter = new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                CompletableFuture.supplyAsync(supplier, taskExecutor)
                        .thenAccept(result -> {
                            completeTask(finalId, JSON.toJSONString(result));
                        }).exceptionally(throwable -> {
                    log.error("", throwable);
                    failTask(finalId, throwable.getMessage());
                    return null;
                });
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(transactionSynchronizationAdapter);
        } else {
            transactionSynchronizationAdapter.afterCommit();
        }


    }


    private void completeTask(String taskId, String response) {
        TaskInfo taskInfo = taskDao.selectById(taskId);
        if (taskInfo != null && taskInfo.getTaskStatus() == TaskInfo.TaskStatus.EXECUTING.getType()) {
            TaskInfo updateTaskInfo = new TaskInfo();
            updateTaskInfo.setId(taskId);
            updateTaskInfo.setTaskStatus(TaskInfo.TaskStatus.SUCCESS.getType());
            updateTaskInfo.setEndExecuteTime(new Date());
            updateTaskInfo.setResponse(response);
            taskDao.update(taskInfo);
        }


    }

    private String initTask(InitTaskReq initTaskReq) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setCreateTime(new Date());
        taskInfo.setInputParams(initTaskReq.getInputParams());
        taskInfo.setIsDeleted(0);
        taskInfo.setTaskType(initTaskReq.getTaskType());
        taskInfo.setTaskStatus(TaskInfo.TaskStatus.CREATED.getType());
        return taskDao.init(taskInfo).getId();

    }

    private void failTask(String taskId, String failureReason) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setId(taskId);
        taskInfo.setTaskStatus(TaskInfo.TaskStatus.FAILED.getType());
        taskInfo.setEndExecuteTime(new Date());
        taskInfo.setFailureReason(failureReason);
        taskDao.update(taskInfo);

    }

    @Transactional
    public void cancelTask(String taskId) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setId(taskId);
        taskInfo.setIsDeleted(1);
        taskDao.update(taskInfo);
        TransactionSynchronizationAdapter transactionSynchronizationAdapter = new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                if (TaskManager.taskThreadMap.containsKey(taskId)) {
                    //线程中断
                    findThreadOptional(TaskManager.taskThreadMap.get(taskId)).ifPresent(thread ->{
                        threadIsTerminatedMap.put(thread.getId(),Boolean.TRUE);
                        thread.interrupt();
                    });
                }else{
                    //广播
                    messageChannel.send(MessageBuilder.withPayload(taskId).build());
                }
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(transactionSynchronizationAdapter);
        } else {
            transactionSynchronizationAdapter.afterCommit();
        }

    }
    @StreamListener(TaskTopic.INPUT)
    public void receive(String taskId) {
        log.info("TaskTopic receive：<{}>", taskId);
        if (TaskManager.taskThreadMap.containsKey(taskId)) {
            findThreadOptional(TaskManager.taskThreadMap.get(taskId)).ifPresent(thread ->{
                threadIsTerminatedMap.put(thread.getId(),Boolean.TRUE);
                thread.interrupt();
            });
            log.info("1-{}",Thread.interrupted());
        }
    }

    public static Optional<Thread> findThreadOptional(long threadId) {
        return Thread.getAllStackTraces().keySet().stream().filter(thread -> thread.getId() == threadId).findFirst();

    }

    /**
     * 判断任务终止状态，可以在内部合适的位置加入判断，如果返回true,逻辑结束任务
     * @return
     */
    public static boolean taskIsTerminated(){
        //        return Thread.currentThread().isInterrupted();//防止外部程序或代码中断异常后未重新中断，舍弃此种方式
        Boolean taskIsTerminated = threadIsTerminatedMap.get(Thread.currentThread().getId()) ;
        return taskIsTerminated != null && taskIsTerminated;
    }

}
