package com.zxl.haze.web.feign;


import com.alibaba.fastjson.JSON;
import com.zxl.haze.core.trace.TraceContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraceInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        template.header(TraceContext.TRACE_JSON, TraceContext.getTraceJson());
        log.error(JSON.toJSONString(template.headers()));
    }
}



