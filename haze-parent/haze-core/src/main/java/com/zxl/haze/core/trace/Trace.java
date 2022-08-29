package com.zxl.haze.core.trace;


import lombok.Data;

import java.util.LinkedList;

@Data
public class Trace {
    private String traceId;
    private LinkedList<Span> spanList = new LinkedList<>();

    protected Span currentSpan() {
        if (!spanList.isEmpty()) {
            return spanList.getLast();
        }
        return new Span(null, "");
    }

    protected void addSpan(String spanName) {
        synchronized (TraceContext.getTraceId()) {
            String spanId = this.currentSpan().getSpanId();
            Span span = new Span(spanId, spanName);
            spanList.add(span);
            if (spanList.size() > 10) {
                spanList.removeLast();
            }
        }
    }
    
}
