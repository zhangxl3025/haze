package com.zxl.haze.mq;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.support.ChannelInterceptor;

@Configuration
@ConditionalOnClass({ChannelInterceptor.class})
public class ChannelInterceptorAutoConfiguration {



    @Bean
    @GlobalChannelInterceptor(patterns = {"*input"})
    @ConditionalOnMissingBean
    SubscribableChannelInterceptor mqInChannelContextInterceptor() {
        return new SubscribableChannelInterceptor();
    }

    @Bean
    @GlobalChannelInterceptor(patterns = {"*output"})
    @ConditionalOnMissingBean
    MessageChannelInterceptor mqOutChannelContextInterceptor() {
        return new MessageChannelInterceptor();
    }
}