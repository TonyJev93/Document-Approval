package com.tonyjev.documentapproval.domain.document.application;

import com.tonyjev.documentapproval.domain.document.domain.Approver;
import com.tonyjev.documentapproval.domain.document.domain.Document;

import java.util.List;

public interface DocumentCreateService {
    Document createDocument(Document document);
}
