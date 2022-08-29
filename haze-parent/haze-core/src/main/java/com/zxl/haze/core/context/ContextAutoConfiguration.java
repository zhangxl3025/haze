package com.zxl.haze.core.context;

import com.zxl.haze.core.context.task.AppContextMapTaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ContextAutoConfiguration {

    @Value("${spring.application.name}")
    private String appName;


    @Bean
    public AppContext appContext() {
        return new AppContext(appName);
    }

    @Bean
    public AppContextMapTaskContext reqTaskContext() {
        return new AppContextMapTaskContext();
    }


}
