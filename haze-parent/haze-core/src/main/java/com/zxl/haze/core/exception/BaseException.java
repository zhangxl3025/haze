package com.zxl.haze.core.exception;

import com.zxl.haze.core.http.Result;
import lombok.Data;

@Data
public class BaseException extends RuntimeException {

    private String errorCode;

    public BaseException() {
        super();
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }


    public BaseException(String message) {
        super(message);
    }

    public BaseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public Result<String> result() {
        return Result.fail(errorCode, this.getMessage());
    }
}
