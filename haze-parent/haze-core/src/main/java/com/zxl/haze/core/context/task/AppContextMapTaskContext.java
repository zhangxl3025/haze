package com.zxl.haze.core.context.task;



import com.zxl.haze.core.context.AppContext;
import com.zxl.haze.core.executor.TaskContext;

import java.util.Map;

public class AppContextMapTaskContext implements TaskContext<Map<String, String>> {

    @Override
    public Map<String, String> getContext() {
        return AppContext.HEADER_MAP.get();
    }

    @Override
    public void setContext(Map<String, String> context) {
        AppContext.HEADER_MAP.set(context);
    }

    public void remove() {
        AppContext.HEADER_MAP.remove();
    }
}
