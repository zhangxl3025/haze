package com.zxl.haze.core.trace;

import com.zxl.haze.core.context.AppContext;
import lombok.Data;

import java.util.UUID;


@Data
public class Span {

    private String appName = AppContext.APP_NAME;
    private String parentSpanId;
    private String spanId;
    private String spanName;
    private long threadId = Thread.currentThread().getId();

    public Span(String parentSpanId, String spanName) {
        this.parentSpanId = parentSpanId;
        this.spanId = threadId + "-" + spanIdSuffix();
        this.spanName = spanName;
    }

    public int spanIdSuffix() {
        if (parentSpanId == null) {
            return 0;
        }
        return Integer.parseInt(parentSpanId.split("-")[1]) + 1;
    }

}
