package com.zxl.haze.mq;


import com.zxl.haze.core.context.AppContext;
import com.zxl.haze.core.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.messaging.DirectWithAttributesChannel;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptor;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SubscribableChannelInterceptor implements ChannelInterceptor {


    @Override
    public Message<?> preSend(@NonNull Message<?> message, MessageChannel messageChannel) {
        try {
            MessageHeaders messageHeaders = message.getHeaders();
            Object traceJsonObj = messageHeaders.get(TraceContext.TRACE_JSON);
            String traceJson = traceJsonObj == null ? null : traceJsonObj.toString();
            String fullChannelName = ((DirectWithAttributesChannel) messageChannel).getFullChannelName();
            TraceContext.traceSpan(traceJson, fullChannelName);
            Map<String, String> userContextMap = new HashMap<>();
            //实际项目需要自行过滤
            for (Map.Entry<String, Object> entry : messageHeaders.entrySet()) {
                userContextMap.put(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
            }
            AppContext.HEADER_MAP.set(userContextMap);
        } catch (Exception e) {
            AppContext.HEADER_MAP.set(null);
        }
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel messageChannel, boolean sent) {
        log.info("In postSend");
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel messageChannel, boolean sent, Exception exception) {
        try {
            AppContext.HEADER_MAP.remove();
        } catch (Exception e) {
            //防御
        } finally {
            try {
                TraceContext.traceEnd();
            } catch (Exception ignored) {

            }

        }
    }
}
