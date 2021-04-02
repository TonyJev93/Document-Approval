package com.tonyjev.documentapproval.domain.document.application.impl;

import com.tonyjev.documentapproval.domain.document.application.DocumentProcessService;
import com.tonyjev.documentapproval.domain.document.application.DocumentRejectService;
import com.tonyjev.documentapproval.domain.document.application.exception.ApproverNotFoundException;
import com.tonyjev.documentapproval.domain.document.application.exception.DocumentNotFoundException;
import com.tonyjev.documentapproval.domain.document.application.validator.DocumentValidator;
import com.tonyjev.documentapproval.domain.document.dao.ApproverRepository;
import com.tonyjev.documentapproval.domain.document.dao.DocumentRepository;
import com.tonyjev.documentapproval.domain.document.domain.Approver;
import com.tonyjev.documentapproval.domain.document.domain.Document;
import com.tonyjev.documentapproval.global.security.utils.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public abstract class DocumentProcessServiceImpl implements DocumentProcessService {

    private final DocumentRepository documentRepository;
    private final ApproverRepository approverRepository;

    @Override
    @Transactional
    public Document processDocument(Long documentId, String comment) {
        String loginUserId = SecurityContextUtil.getLoginUserId();

        // 결재 대상 문서 조회
        Document targetDocument = documentRepository.findById(documentId).orElseThrow(DocumentNotFoundException::new);

        // 결재자 목록 조회
        List<Approver> approverList = approverRepository.findByDocumentIdOrderBySeqAsc(documentId);

        // 문서 결재 관련 유효성 체크
        DocumentValidator.validateApproval(targetDocument, approverList, loginUserId);

        /* 결재 진행 */
        // 현재 결재자 추출
        Approver currentApprover = Approver.extractCurrentApprover(approverList, loginUserId).orElseThrow(ApproverNotFoundException::new);

        updateApprovalStatus(comment, approverList, currentApprover);

        // 문서 상태 변경 ( if 모든 결재자 승인완료 = 결재중 -> 승인 )
        targetDocument.changeApprovalStatus(approverList);

        return targetDocument;
    }

    protected abstract void updateApprovalStatus(String comment, List<Approver> approverList, Approver currentApprover);

}
