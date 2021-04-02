package com.tonyjev.documentapproval.domain.document.dao;

import com.tonyjev.documentapproval.domain.document.domain.Document;
import com.tonyjev.documentapproval.domain.document.domain.code.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByCreateUserIdAndApprovalStatus(String userId, ApprovalStatus approvalStatus);

    List<Document> findAllByIdInAndApprovalStatus(List<Long> documentIdList, ApprovalStatus approvalStatus);
}
