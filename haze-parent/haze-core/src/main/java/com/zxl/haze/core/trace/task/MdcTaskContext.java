package com.zxl.haze.core.trace.task;


import com.zxl.haze.core.executor.TaskContext;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;


public class MdcTaskContext implements TaskContext<Map<String, String>> {

    @Override
    public Map<String, String> getContext() {
        if (MDC.getCopyOfContextMap() == null) {
            return new HashMap<>();
        }
        return MDC.getCopyOfContextMap();
    }

    @Override
    public void setContext(Map<String, String> context) {
        MDC.setContextMap(context);
    }

    public void remove() {
        MDC.clear();
    }
}
