package com.zxl.haze.core.executor.support;


import com.zxl.haze.core.executor.TaskContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TaskContextUtil implements ApplicationContextAware {


    private static ApplicationContext applicationContext;

    @SuppressWarnings("all")
    public static Map<String, Object> replay() {
        Map<String, TaskContext> contextHolderMap = applicationContext.getBeansOfType(TaskContext.class);
        Map<String, Object> contextMap = new HashMap<>(contextHolderMap.size());
        for (Map.Entry<String, TaskContext> entry : contextHolderMap.entrySet()) {
            Object context = entry.getValue().getContext();
            contextMap.put(entry.getKey(), context);
        }
        return contextMap;
    }

    @SuppressWarnings("all")
    public static void restore(Map<String, Object> contextMap) {
        Map<String, TaskContext> contextHolderMap = applicationContext.getBeansOfType(TaskContext.class);
        for (Map.Entry<String, TaskContext> entry : contextHolderMap.entrySet()) {
            entry.getValue().setContext(contextMap.get(entry.getKey()));
        }
    }

    @SuppressWarnings("all")
    public static void clear() {
        Map<String, TaskContext> contextHolderMap = applicationContext.getBeansOfType(TaskContext.class);
        for (TaskContext taskSPI : contextHolderMap.values()) {
            taskSPI.remove();
        }
    }


    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        TaskContextUtil.applicationContext = applicationContext;
    }
}
