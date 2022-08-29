package com.zxl.haze.web.exception;

import com.zxl.haze.core.http.Result;

public enum ExceptionEnum {

    ERROR("500", "服务器内部错误");

    public String errorCode;
    public String desc;

    ExceptionEnum(String errorCode, String desc) {
        this.errorCode = errorCode;
        this.desc = desc;
    }


    public Result<String> result() {
        return Result.fail(errorCode, desc);
    }


}
