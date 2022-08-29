package com.zxl.haze.core.trace;

import com.zxl.haze.core.context.AppContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
public class Span {

    private String appName = AppContext.APP_NAME;
    private int spanId;
    private String spanName;
    private long threadId = Thread.currentThread().getId();

    public Span(int spanId, String spanName) {
        this.spanId = spanId;
        this.spanName = spanName;
    }

}
