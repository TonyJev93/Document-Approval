package com.tonyjev.documentapproval.domain.document.application.impl;

import com.tonyjev.documentapproval.domain.document.application.DocumentRetrieveService;
import com.tonyjev.documentapproval.domain.document.application.exception.DocumentNotFoundException;
import com.tonyjev.documentapproval.domain.document.dao.ApproverRepository;
import com.tonyjev.documentapproval.domain.document.dao.DocumentRepository;
import com.tonyjev.documentapproval.domain.document.domain.Approver;
import com.tonyjev.documentapproval.domain.document.domain.Document;
import com.tonyjev.documentapproval.domain.document.domain.code.RetrieveType;
import com.tonyjev.documentapproval.global.security.utils.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.tonyjev.documentapproval.domain.document.domain.code.ApprovalStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentRetrieveServiceImpl implements DocumentRetrieveService {

    private final DocumentRepository documentRepository;
    private final ApproverRepository approverRepository;

    @Override
    public List<Document> retrieveDocumentList(RetrieveType retrieveType) {
        String loginUserId = SecurityContextUtil.getLoginUserId();

        List<Document> searchedDocumentList = new ArrayList<>();

        switch (retrieveType) {
            case outbox: // 내가 생성한 문서 중 결재 진행 중인 문서
                searchedDocumentList = getOutboxDocumentList(loginUserId);
                break;
            case inbox: // 내가 결재를 해야 할 문서
                searchedDocumentList = getInboxDocumentList(loginUserId);
                break;
            case archive: // 내가 관여한 문서 중 결재가 완료(승인 또는 거절)된 문서
                searchedDocumentList = getArchiveDocumentList(loginUserId);
                break;
            default:
                break;
        }
        return searchedDocumentList;
    }

    @Override
    public Document retrieveDocument(Long documentId) {
        return documentRepository.findById(documentId).orElseThrow(DocumentNotFoundException::new);
    }

    private List<Document> getOutboxDocumentList(String loginUserId) {
        return documentRepository.findByCreateUserIdAndApprovalStatus(loginUserId, APPROVING);
    }

    private List<Document> getArchiveDocumentList(String loginUserId) {
        // find by userId from approver table
        // 본인이 관여한 결재 목록 조회
        List<Approver> myApprovalList = approverRepository.findByUserIdAndApprovalYn(loginUserId, true);

        // transfer to document List by document ID List
        List<Long> myApprovingDocumentIdList = Approver.extractDocumentIdList(myApprovalList);

        // Get Document List By Id List
        List<Document> myApprovingDocumentList = documentRepository.findAllById(myApprovingDocumentIdList);

        // filter status equals approved or rejected
        return myApprovingDocumentList.stream()
                .filter(document ->
                        document.getApprovalStatus().equals(APPROVED) || document.getApprovalStatus().equals(REJECTED))
                .collect(Collectors.toList());
    }

    private List<Document> getInboxDocumentList(String loginUserId) {
        // 로그인 한 사용자가 결재 가능한 결재 리스트
        List<Approver> myApprovalTodoList = approverRepository.findByUserIdAndApprovalStatus(loginUserId, APPROVING);

        // 조회 대상 문서 ID 추출
        List<Long> approvalTargetDocumentIdList = Approver.extractDocumentIdList(myApprovalTodoList);

        // 미완료 된 결재 대상 문서 조회
        return documentRepository.findAllByIdInAndApprovalStatus(approvalTargetDocumentIdList, APPROVING);
    }

}
