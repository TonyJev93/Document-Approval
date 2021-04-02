package com.tonyjev.documentapproval.domain.document.application.exception;

import com.tonyjev.documentapproval.global.exception.ErrorCode;
import com.tonyjev.documentapproval.global.exception.common.BusinessException;

public class DocumentApprovalNotPossibleException extends BusinessException {
    public DocumentApprovalNotPossibleException() {
        super(ErrorCode.DOCUMENT_APPROVAL_NOT_POSSIBLE);
    }
}
