package com.zxl.haze.core.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AppContext implements EnvironmentAware, ApplicationContextAware {


    public static String APP_NAME;
    public static Environment environment;
    public static ApplicationContext applicationContext;
    public static final ThreadLocal<Map<String, String>> HEADER_MAP = ThreadLocal.withInitial(HashMap::new);



    public AppContext(String APP_NAME) {
        AppContext.APP_NAME = APP_NAME;
    }


    @Override
    public void setEnvironment(@NonNull Environment environment) {
        AppContext.environment = environment;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        AppContext.applicationContext = applicationContext;
    }


    public static String[] getActiveProfiles() {
        String[] profiles = environment.getActiveProfiles();
        if (profiles.length == 0) {
            return environment.getDefaultProfiles();
        }
        return profiles;
    }
}
