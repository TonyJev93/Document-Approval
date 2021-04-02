package com.tonyjev.documentapproval.domain.document.application.exception;

import com.tonyjev.documentapproval.global.exception.ErrorCode;
import com.tonyjev.documentapproval.global.exception.common.BusinessException;

public class DocumentNotFoundException extends BusinessException {
    public DocumentNotFoundException() {
        super(ErrorCode.NOT_FOUND_DOCUMENT);
    }
}
