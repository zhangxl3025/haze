package com.zxl.haze.task.topic;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface TaskTopic {
    String OUTPUT = "task-output";
    String INPUT = "task-input";

    @Output(TaskTopic.OUTPUT)
    MessageChannel output();

    @Input(TaskTopic.INPUT)
    SubscribableChannel input();
}
