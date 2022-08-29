package com.zxl.haze.web.feign;

import feign.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Slf4j
@Configuration
public class OpenFeignAutoConfiguration {

    @Bean
    @ConditionalOnBean
    public feign.Logger.Level feignLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    @ConditionalOnBean
    public feign.Logger feignLogger() {
        return new Logger() {
            @Override
            protected void log(String configKey, String format, Object... args) {
                log.info(String.format(methodTag(configKey) + format, args));
            }
        };
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public TraceInterceptor traceInterceptor() {
        return new TraceInterceptor();
    }


}
