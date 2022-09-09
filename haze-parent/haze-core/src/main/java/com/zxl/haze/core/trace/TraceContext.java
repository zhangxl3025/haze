package com.zxl.haze.core.trace;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.LinkedList;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public final class TraceContext {

    public static final ThreadLocal<Trace> CONTEXT = ThreadLocal.withInitial(() -> {
        Trace trace = new Trace();
        trace.setTraceId(generateTraceId());
        return trace;
    });
    public static final String APP_NAME = "app-name";
    public static final String TRACE_ID = "trace-id";
    private static final String SPAN_ID = "span-id";
    private static final String PARENT_SPAN_ID = "parent-span-id";
    private static final String SPAN_NAME = "span-name";
    public static final String TRACE_JSON = "trace-json";


    public static Trace getTrace() {
        return CONTEXT.get();
    }



    public static String getTraceJson() {
        return JSONObject.toJSONString(CONTEXT.get());
    }

    public static void traceSpan(String spanName) {
        synchronized (TraceContext.getTraceId()) {
            Trace trace = getTrace();
            trace.addSpan(spanName);
            put2Mdc(trace);
        }

    }


    public static void closeSpan() {
        synchronized (TraceContext.getTraceId()) {
            Trace trace = getTrace();
            LinkedList<Span> spanList = trace.getSpanList();
            int index = spanList.stream().map(Span::getThreadId).collect(Collectors.toList()).lastIndexOf(Thread.currentThread().getId());
            if (index > 0){
                spanList.remove(index);
                put2Mdc(trace);
            }
        }
    }


    private static void put2Mdc(Trace trace) {
        MDC.put(TRACE_ID, trace.getTraceId());
        MDC.put(SPAN_ID, trace.currentSpan().getSpanId());
        MDC.put(SPAN_NAME, trace.currentSpan().getSpanName());
        MDC.put(PARENT_SPAN_ID, trace.currentSpan().getParentSpanId());
    }

    /**
     * 开启trace
     *
     * @param traceJson
     * @param spanName
     */
    public static void traceSpan(String traceJson, String spanName) {
        setTrace(traceJson);
        traceSpan(spanName);

    }


    /**
     * 关闭trace
     */
    public static void traceEnd() {
        try {
            CONTEXT.remove();
        } finally {
            MDC.clear();
        }
    }


    private static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 从字符串中解析Trace
     *
     * @param traceJson
     * @return
     */
    public static void setTrace(String traceJson) {
        if (JSONObject.isValidObject(traceJson)) {
            Trace trace = JSONObject.parseObject(traceJson, Trace.class);
            CONTEXT.set(trace);
            put2Mdc(trace);
        } else {
            Trace trace = CONTEXT.get();
            put2Mdc(trace);
            log.info("traceJson [{}] not valid", traceJson);
        }
    }


    public static String getTraceId() {
        return TraceContext.getTrace().getTraceId();
    }
}
