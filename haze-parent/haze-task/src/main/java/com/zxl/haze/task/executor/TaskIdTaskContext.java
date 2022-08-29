package com.zxl.haze.task.executor;

import com.zxl.haze.core.executor.TaskContext;
import com.zxl.haze.task.manager.TaskManager;
import org.springframework.stereotype.Component;

@Component
public class TaskIdTaskContext implements TaskContext<String> {

    @Override
    public String getContext() {
        return TaskManager.getTaskId();
    }

    @Override
    public void setContext(String context) {
        TaskManager.setTaskId(context);
    }

    @Override
    public void remove() {
        TaskManager.removeTaskId();
    }
}
