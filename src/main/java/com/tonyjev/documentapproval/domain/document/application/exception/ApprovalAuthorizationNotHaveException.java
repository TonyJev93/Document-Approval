package com.tonyjev.documentapproval.domain.document.application.exception;

import com.tonyjev.documentapproval.global.exception.ErrorCode;
import com.tonyjev.documentapproval.global.exception.common.BusinessException;

public class ApprovalAuthorizationNotHaveException extends BusinessException {
    public ApprovalAuthorizationNotHaveException() {
        super(ErrorCode.APPROVAL_TURN_NOT_MINE);
    }
}
