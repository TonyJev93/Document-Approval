package com.tonyjev.documentapproval.domain.document.application.impl;

import com.tonyjev.documentapproval.domain.document.application.DocumentRejectService;
import com.tonyjev.documentapproval.domain.document.dao.ApproverRepository;
import com.tonyjev.documentapproval.domain.document.dao.DocumentRepository;
import com.tonyjev.documentapproval.domain.document.domain.Approver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

// Template Method Pattern(문서 반려)
@Service
@Slf4j
public class DocumentRejectServiceImpl extends DocumentProcessServiceImpl implements DocumentRejectService {

    public DocumentRejectServiceImpl(DocumentRepository documentRepository, ApproverRepository approverRepository) {
        super(documentRepository, approverRepository);
    }

    // 반려 처리 구현
    @Override
    protected void updateApprovalStatus(String comment, List<Approver> approverList, Approver currentApprover) {
        // 반려 처리(결재중 -> 반려, 첨언 추가)
        currentApprover.reject(comment);
    }


}
