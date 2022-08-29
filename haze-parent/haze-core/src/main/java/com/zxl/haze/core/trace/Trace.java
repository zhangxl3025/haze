package com.zxl.haze.core.trace;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

@Data
public class Trace {
    private String traceId;
    private LinkedList<Span> spanList = new LinkedList<>();

    protected Span currentSpan() {
        if (!spanList.isEmpty()) {
            return spanList.getLast();
        }
        return new Span(0, "");
    }


    protected int currentSpanId() {
        return currentSpan().getSpanId();
    }

    protected String currentSpanName() {
        return currentSpan().getSpanName();
    }

    protected String currentSpanIdString() {
        return String.valueOf(currentSpanId());
    }


    protected void addSpan(String spanName) {
        synchronized (TraceContext.getTraceId()) {
            int spanId = this.currentSpanId();
            Span span = new Span(++spanId, spanName);
            spanList.add(span);
            if (spanList.size() > 10) {
                spanList.removeLast();
            }
        }
    }

}
