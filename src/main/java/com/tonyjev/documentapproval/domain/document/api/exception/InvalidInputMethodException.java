package com.tonyjev.documentapproval.domain.document.api.exception;

import com.tonyjev.documentapproval.global.exception.ErrorCode;
import com.tonyjev.documentapproval.global.exception.common.BusinessException;

public class InvalidInputMethodException extends BusinessException {
    public InvalidInputMethodException() {
        super(ErrorCode.INVALID_INPUT_METHOD);
    }
}
