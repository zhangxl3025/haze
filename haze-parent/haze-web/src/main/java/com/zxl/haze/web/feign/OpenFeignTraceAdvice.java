//package com.zxl.haze.web.feign;
//
//import com.zxl.haze.core.exception.BizException;
//import com.zxl.haze.core.exception.SystemException;
//import com.zxl.haze.core.trace.TraceContext;
//import feign.Feign;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.lang.reflect.Method;
//import java.util.Arrays;
//import java.util.concurrent.atomic.AtomicReference;
//
//@Slf4j
//@Aspect
//public class OpenFeignTraceAdvice {
//
//
//    AtomicReference<String> spanKeyAtomicReference = new AtomicReference<>();
//
//
//    @Around("@within(org.springframework.cloud.openfeign.FeignClient)")
//    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method method = signature.getMethod();
//        String logName = method.getDeclaringClass().getName() + "." + method.getName();
//        String spanName = Feign.configKey(signature.getDeclaringType(), method);
//        String spanKey = TraceContext.traceSpan(spanName);
//        spanKeyAtomicReference.set(spanKey);
//        Logger log = LoggerFactory.getLogger(logName);
//        Object[] args = joinPoint.getArgs();
//        log.info("param :<{}>", Arrays.toString(args));
//        long start = System.currentTimeMillis();
//        Object result = joinPoint.proceed();
//        long duration = System.currentTimeMillis() - start;
//        log.info("result:<{}> duration:{}ms", result, duration);
//        return result;
//    }
//
//    @AfterReturning(pointcut = "@within(org.springframework.cloud.openfeign.FeignClient)")
//    public void doAfterReturning() {
//        TraceContext.closeSpan(spanKeyAtomicReference.get());
//    }
//
//    @AfterThrowing(pointcut = "@within(org.springframework.cloud.openfeign.FeignClient)", throwing = "e", argNames = "e,joinPoint")
//    public void doAfterThrowing(Throwable e, ProceedingJoinPoint joinPoint) throws Throwable {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        Method method = signature.getMethod();
//        String logName = method.getDeclaringClass().getName() + "." + method.getName();
//        String spanName = Feign.configKey(signature.getDeclaringType(), method);
//        Logger log = LoggerFactory.getLogger(logName);
//        log.warn("spanName：{},trace:{}", spanName,TraceContext.getTraceLog(), e);
//        TraceContext.closeSpan(spanKeyAtomicReference.get());
//        throw e;
//    }
//
//}
