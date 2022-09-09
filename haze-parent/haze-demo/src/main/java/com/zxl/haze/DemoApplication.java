package com.zxl.haze;

import com.zxl.haze.core.http.Result;
import com.zxl.haze.core.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@SpringBootApplication
@EnableAsync
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
    @Resource
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @RequestMapping("hello")
    public Result<?> hello() throws InterruptedException {
        TraceContext.traceSpan("method hello");
        CompletableFuture.runAsync(() ->{
            CompletableFuture.runAsync(() ->{
                CompletableFuture.runAsync(() ->{
                },threadPoolTaskExecutor);
            },threadPoolTaskExecutor);
            CompletableFuture.runAsync(() ->{
            },threadPoolTaskExecutor);
        },threadPoolTaskExecutor);

        log.info("hello invoking");
        TraceContext.closeSpan();
        return Result.ok("hello");
    }


}
