package com.zxl.haze.core.http;




import com.zxl.haze.core.context.AppContext;

import com.zxl.haze.core.trace.TraceContext;
import lombok.Data;

@Data
public class Result<T> {

    private String appName;
    private String traceId;


    private String errorCode;
    private T result;
    private String message;



    public static <T> Result<T> ok(T result) {
        Result<T> baseResponse = new Result<>(result);
        baseResponse.setErrorCode("200");
        return baseResponse;
    }

    public static Result<String> fail(String errorCode, String message) {
        Result<String> result = new Result<>();
        result.setMessage(message);
        result.setErrorCode(errorCode);
        return result;
    }

    private Result() {
        this.appName = AppContext.APP_NAME;
        this.traceId = TraceContext.getTraceId();
    }

    private Result(T result) {
        this.appName = AppContext.APP_NAME;
        this.traceId = TraceContext.getTraceId();
        this.result = result;
    }


    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
