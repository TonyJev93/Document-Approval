package com.tonyjev.documentapproval.global.exception.common;


import com.tonyjev.documentapproval.global.exception.ErrorCode;

public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private ErrorCode errorCode;

    public BusinessException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;

    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
