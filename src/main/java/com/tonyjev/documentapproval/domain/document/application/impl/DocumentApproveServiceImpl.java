package com.tonyjev.documentapproval.domain.document.application.impl;

import com.tonyjev.documentapproval.domain.document.application.DocumentApproveService;
import com.tonyjev.documentapproval.domain.document.dao.ApproverRepository;
import com.tonyjev.documentapproval.domain.document.dao.DocumentRepository;
import com.tonyjev.documentapproval.domain.document.domain.Approver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

// Template Method Pattern (문서 승인)
@Service
@Slf4j
public class DocumentApproveServiceImpl extends DocumentProcessServiceImpl implements DocumentApproveService {

    public DocumentApproveServiceImpl(DocumentRepository documentRepository, ApproverRepository approverRepository) {
        super(documentRepository, approverRepository);
    }

    // 승인 처리 구현
    @Override
    protected void updateApprovalStatus(String comment, List<Approver> approverList, Approver currentApprover) {
        // 승인 처리(결재중 -> 승인, 첨언 추가)
        currentApprover.approve(comment);

        // 다음 결재자 상태 변경
        Approver.updateNextApproverStatus(approverList);
    }


}
