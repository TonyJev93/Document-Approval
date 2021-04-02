package com.tonyjev.documentapproval.domain.document.application;

import com.tonyjev.documentapproval.domain.document.domain.Document;
import com.tonyjev.documentapproval.domain.document.domain.code.RetrieveType;

import java.util.List;

public interface DocumentRetrieveService {

    List<Document> retrieveDocumentList(RetrieveType retrieveType);

    Document retrieveDocument(Long documentId);
}
