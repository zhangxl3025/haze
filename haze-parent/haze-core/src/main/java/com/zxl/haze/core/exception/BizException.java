//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zxl.haze.core.exception;

import lombok.Data;

@Data
public class BizException extends BaseException {

    public BizException() {
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String errorCode, String message) {
        super(errorCode, message);
    }
}
