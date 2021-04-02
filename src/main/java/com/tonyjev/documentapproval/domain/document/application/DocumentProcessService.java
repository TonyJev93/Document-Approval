package com.tonyjev.documentapproval.domain.document.application;

import com.tonyjev.documentapproval.domain.document.domain.Document;

public interface DocumentProcessService {
    Document processDocument(Long documentId, String comment);
}
