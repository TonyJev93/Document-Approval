package com.tonyjev.documentapproval.domain.user.application.exception;

import com.tonyjev.documentapproval.global.exception.ErrorCode;
import com.tonyjev.documentapproval.global.exception.common.BusinessException;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(ErrorCode.NOT_FOUND_USER);
    }
}
