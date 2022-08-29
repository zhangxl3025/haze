//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zxl.haze.core.exception;

import lombok.Data;

@Data
public class SystemException extends BaseException {
    public SystemException() {
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String errorCode, String message) {
        super(errorCode, message);
    }
}
