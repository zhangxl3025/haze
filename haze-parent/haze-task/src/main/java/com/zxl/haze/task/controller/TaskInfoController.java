package com.zxl.haze.task.controller;

import com.zxl.haze.core.http.Result;
import com.zxl.haze.task.domain.InitTaskReq;
import com.zxl.haze.task.domain.TaskInfo;
import com.zxl.haze.task.manager.TaskManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class TaskInfoController {

    @Resource
    TaskManager taskManager;



     @RequestMapping("/export")
    public Result<String> export(@RequestParam("exportParam") String exportParam) {
        InitTaskReq req = new InitTaskReq();
        req.setInputParams(exportParam);
        req.setTaskType(TaskInfo.TaskType.FILE_EXPORT.getType());
        taskManager.addTask(() -> {
            task();
            return null;
        }, req);
        log.info(TaskManager.getTaskId());


        return Result.ok(TaskManager.getTaskId());
    }


    private void task(){
        log.info("task is begin");
//        try {
//            TimeUnit.MINUTES.sleep(5);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//        if (TaskManager.taskIsTerminated()){
//            log.info("线程已中断-{}",Thread.currentThread().isInterrupted());
//            return;
//        }
        doSomething();
    }

    private void doSomething(){
        log.info("task is end");
    }

    @RequestMapping("/cancel")
    public Result<Boolean> cancel(@RequestParam("taskId") String taskId) {
        taskManager.cancelTask(taskId);
        return Result.ok(Boolean.TRUE);
    }
}
