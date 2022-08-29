package com.zxl.haze.core.trace.task;


import com.zxl.haze.core.trace.Trace;
import com.zxl.haze.core.trace.TraceContext;
import com.zxl.haze.core.executor.TaskContext;

import java.util.concurrent.atomic.AtomicReference;

public class TraceTaskContext implements TaskContext<Trace> {



    @Override
    public Trace getContext() {
        return TraceContext.CONTEXT.get();
    }

    @Override
    public void setContext(Trace context) {
        TraceContext.CONTEXT.set(context);
        TraceContext.traceSpan(Thread.currentThread().getName());
    }

    public void remove() {
        try {
            TraceContext.closeSpan();
        } finally {
            TraceContext.CONTEXT.remove();
        }
    }

}
