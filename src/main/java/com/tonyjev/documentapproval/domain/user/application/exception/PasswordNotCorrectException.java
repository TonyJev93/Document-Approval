package com.tonyjev.documentapproval.domain.user.application.exception;

import com.tonyjev.documentapproval.global.exception.ErrorCode;
import com.tonyjev.documentapproval.global.exception.common.BusinessException;

public class PasswordNotCorrectException extends BusinessException {
    public PasswordNotCorrectException() {
        super(ErrorCode.PASSWORD_NOT_CORRECT);
    }
}
