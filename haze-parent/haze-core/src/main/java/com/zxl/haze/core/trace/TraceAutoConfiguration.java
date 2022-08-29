package com.zxl.haze.core.trace;


import com.zxl.haze.core.trace.task.MdcTaskContext;
import com.zxl.haze.core.trace.task.TraceTaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TraceAutoConfiguration {



    @Bean
    public MdcTaskContext mdcTaskContext() {
        return new MdcTaskContext();
    }

    @Bean
    public TraceTaskContext traceTaskContext() {
        return new TraceTaskContext();
    }
}
