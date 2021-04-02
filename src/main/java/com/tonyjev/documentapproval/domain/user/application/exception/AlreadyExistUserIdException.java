package com.tonyjev.documentapproval.domain.user.application.exception;

import com.tonyjev.documentapproval.global.exception.ErrorCode;
import com.tonyjev.documentapproval.global.exception.common.BusinessException;

public class AlreadyExistUserIdException extends BusinessException {
    public AlreadyExistUserIdException() {
        super(ErrorCode.ALREADY_EXIST_USER_ID_EXCEPTION);
    }
}
