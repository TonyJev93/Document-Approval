package com.tonyjev.documentapproval.domain.document.application.exception;

import com.tonyjev.documentapproval.global.exception.ErrorCode;
import com.tonyjev.documentapproval.global.exception.common.BusinessException;

public class AlreadyApprovedApproverException extends BusinessException {
    public AlreadyApprovedApproverException() {
        super(ErrorCode.ALREADY_APPROVED_APPROVER);
    }
}
