package com.zxl.haze.core.executor.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;


@Slf4j
@Component
public class DynamicConfigApplicationListener implements ApplicationListener<RefreshEvent>,
        ApplicationContextAware, Ordered {


    private ContextRefresher contextRefresher;

    private ApplicationContext context;

    public DynamicConfigApplicationListener(ContextRefresher contextRefresher) {
        this.contextRefresher = contextRefresher;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.context = applicationContext;
    }

    @Override
    public void onApplicationEvent(@NonNull RefreshEvent event) {
        ConfigurableEnvironment beforeEnv = (ConfigurableEnvironment) context.getEnvironment();
        MutablePropertySources propertySources = beforeEnv.getPropertySources();
        MutablePropertySources beforeSources = new MutablePropertySources(propertySources);
        // 刷新上下文
        Set<String> refresh = this.contextRefresher.refresh();
        // 获取对比值发布事件
        Map<String, Map<String, String>> contrast = PropertyUtil.contrastAll(beforeSources, propertySources);
        context.publishEvent(new ActionConfigEvent(this, "Refresh config", contrast));
        log.info("[ActionApplicationListener] The update is successful {}", refresh);
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE - 1;
    }

}
