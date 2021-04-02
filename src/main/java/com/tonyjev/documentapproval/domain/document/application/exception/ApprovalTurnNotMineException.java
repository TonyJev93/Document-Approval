package com.tonyjev.documentapproval.domain.document.application.exception;

import com.tonyjev.documentapproval.global.exception.ErrorCode;
import com.tonyjev.documentapproval.global.exception.common.BusinessException;

public class ApprovalTurnNotMineException extends BusinessException {
    public ApprovalTurnNotMineException() {
        super(ErrorCode.APPROVAL_TURN_NOT_MINE);
    }
}
