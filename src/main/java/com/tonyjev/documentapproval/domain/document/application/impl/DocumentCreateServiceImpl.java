package com.tonyjev.documentapproval.domain.document.application.impl;

import com.tonyjev.documentapproval.domain.document.application.DocumentCreateService;
import com.tonyjev.documentapproval.domain.document.application.validator.DocumentValidator;
import com.tonyjev.documentapproval.domain.document.dao.ApproverRepository;
import com.tonyjev.documentapproval.domain.document.dao.DocumentRepository;
import com.tonyjev.documentapproval.domain.document.domain.Approver;
import com.tonyjev.documentapproval.domain.document.domain.Document;
import com.tonyjev.documentapproval.domain.user.dao.UserRepository;
import com.tonyjev.documentapproval.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentCreateServiceImpl implements DocumentCreateService {

    private final DocumentRepository documentRepository;
    private final ApproverRepository approverRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Document createDocument(Document document) {

        log.info("Start Document Create Service..");
        documentRepository.save(document);

        List<Approver> approverList = document.getApproverList();

        // 회원 목록 조회 by 결재자 목록
        List<User> joinUserList = userRepository.findAllByUserIdIn(Approver.getUserIdList(approverList));

        // 결재자 목록 검증
        DocumentValidator.validateCreate(approverList, joinUserList);

        // 문서 ID 맵핑 to 결재자
        mappingDocumentIdToApprover(approverList, document.getId());

        // 결재자 저장
        approverRepository.saveAll(approverList);

        return document;
    }

    private void mappingDocumentIdToApprover(List<Approver> approverEntityList, Long documentId) {
        approverEntityList.forEach(approver -> approver.updateDocumentId(documentId));
    }
}
