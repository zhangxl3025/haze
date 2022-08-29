package com.zxl.haze.mq;


import com.zxl.haze.core.context.AppContext;
import com.zxl.haze.core.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.messaging.DirectWithAttributesChannel;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
public class MessageChannelInterceptor implements ChannelInterceptor {



    @Override
    public Message<?> preSend(@NonNull Message<?> message, MessageChannel messageChannel) {
        String fullChannelName = ((DirectWithAttributesChannel) messageChannel).getFullChannelName();
        TraceContext.traceSpan(fullChannelName);
        MessageBuilder<?> messageBuilder = MessageBuilder.fromMessage(message);
        messageBuilder.setHeader(TraceContext.TRACE_JSON, TraceContext.getTraceJson());
        for (Map.Entry<String, String> entry : AppContext.HEADER_MAP.get().entrySet()) {
            messageBuilder.setHeader(entry.getKey(), entry.getValue());
        }
        return messageBuilder.build();
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel messageChannel, boolean sent, Exception exception) {
        try {
            TraceContext.closeSpan();
        } catch (Exception e) {
            //防御
        } finally {
        }
    }
}
