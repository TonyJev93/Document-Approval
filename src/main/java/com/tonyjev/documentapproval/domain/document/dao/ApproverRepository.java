package com.tonyjev.documentapproval.domain.document.dao;

import com.tonyjev.documentapproval.domain.document.domain.Approver;
import com.tonyjev.documentapproval.domain.document.domain.ApproverPk;
import com.tonyjev.documentapproval.domain.document.domain.code.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApproverRepository extends JpaRepository<Approver, ApproverPk> {
    List<Approver> findByUserIdAndApprovalYn(String userId, boolean approvalYn);

    List<Approver> findByUserIdAndApprovalStatus(String userId, ApprovalStatus approvalStatus);

    List<Approver> findByDocumentIdOrderBySeqAsc(Long documentId);
}
