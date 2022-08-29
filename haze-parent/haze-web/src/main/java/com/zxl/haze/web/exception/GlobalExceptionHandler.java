package com.zxl.haze.web.exception;

import com.zxl.haze.core.exception.BizException;
import com.zxl.haze.core.exception.SystemException;
import com.zxl.haze.core.http.Result;
import com.zxl.haze.core.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.UndeclaredThrowableException;

//import feign.RetryableException;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 业务异常输出级别warn
     * @param e
     * @param response
     * @return
     */
    @ExceptionHandler(BizException.class)
    @ResponseBody
    public Result<String> exceptionHandler(BizException e, HttpServletResponse response) {
        log.warn("trace：{}", TraceContext.getTraceLog(), e);
        return e.result();
    }
    @ExceptionHandler(TaskRejectedException.class)
    @ResponseBody
    public Result<String> exceptionHandler(TaskRejectedException e, HttpServletResponse response) {
        log.error("trace：{}", TraceContext.getTraceLog(), e);
        return Result.fail("线程池拒绝", e.getMessage());
    }


    @ExceptionHandler(SystemException.class)
    @ResponseBody
    public Result<String> exceptionHandler(SystemException e, HttpServletResponse response) {
        log.error("trace：{}", TraceContext.getTraceLog(), e);
        return e.result();
    }


    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result<String> exceptionHandler(RuntimeException e, HttpServletResponse response) {
        if (e instanceof UndeclaredThrowableException) {
            Throwable ex = ((UndeclaredThrowableException) e).getUndeclaredThrowable();
            if (ex instanceof BizException) {
                return exceptionHandler((BizException) ex, response);
            }
            if (ex instanceof SystemException) {
                return exceptionHandler((SystemException) ex, response);
            }
        }
        log.error("trace：{}", TraceContext.getTraceLog(), e);
        return ExceptionEnum.ERROR.result();
    }


}
