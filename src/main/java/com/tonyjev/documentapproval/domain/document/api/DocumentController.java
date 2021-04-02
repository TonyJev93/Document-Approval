package com.tonyjev.documentapproval.domain.document.api;


import com.tonyjev.documentapproval.domain.document.api.dto.request.DocumentApproveRequest;
import com.tonyjev.documentapproval.domain.document.api.dto.request.DocumentCreateRequest;
import com.tonyjev.documentapproval.domain.document.api.dto.request.DocumentProcessMethod;
import com.tonyjev.documentapproval.domain.document.api.dto.response.DocumentDto;
import com.tonyjev.documentapproval.domain.document.api.exception.InvalidInputMethodException;
import com.tonyjev.documentapproval.domain.document.application.DocumentApproveService;
import com.tonyjev.documentapproval.domain.document.application.DocumentCreateService;
import com.tonyjev.documentapproval.domain.document.application.DocumentRejectService;
import com.tonyjev.documentapproval.domain.document.application.DocumentRetrieveService;
import com.tonyjev.documentapproval.domain.document.domain.Document;
import com.tonyjev.documentapproval.domain.document.domain.code.RetrieveType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0/user/document")
@Slf4j
public class DocumentController {

    private final DocumentCreateService documentCreateService;
    private final DocumentRetrieveService documentRetrieveService;
    private final DocumentApproveService documentApproveService;
    private final DocumentRejectService documentRejectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentDto createDocument(@Valid @RequestBody DocumentCreateRequest request) {
        log.info("문서 생성 시작");
        return DocumentDto.of(documentCreateService.createDocument(request.toEntity()));
    }

    @GetMapping("/list/{retrieveType}")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> getDocumentList(@PathVariable RetrieveType retrieveType) {
        log.info("문서 목록({}) 조회 시작", retrieveType);
        Map<String, Object> result = new HashMap<>();
        List<Document> documentList = documentRetrieveService.retrieveDocumentList(retrieveType);

        List<DocumentDto> documentDtoList = new ArrayList<>();
        documentList.forEach(document -> documentDtoList.add(DocumentDto.of(document)));

        result.put("total", documentDtoList.size());
        result.put("documentList", documentDtoList);
        return result;
    }

    @GetMapping("/{documentId}")
    @ResponseStatus(HttpStatus.OK)
    public DocumentDto getDocument(@PathVariable Long documentId) {
        log.info("문서 조회 시작");
        return DocumentDto.of(documentRetrieveService.retrieveDocument(documentId));
    }

    @PatchMapping("/{documentId}/{method}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approveDocument(@Valid @PathVariable Long documentId, @Valid @PathVariable DocumentProcessMethod method,
                                @RequestBody DocumentApproveRequest request) {
        if (method.equals(DocumentProcessMethod.approve)) {
            log.info("문서 승인 시작");
            documentApproveService.processDocument(documentId, request.getComment());
        } else if (method.equals(DocumentProcessMethod.reject)) {
            log.info("문서 반려 시작");
            documentRejectService.processDocument(documentId, request.getComment());
        } else {
            throw new InvalidInputMethodException();
        }
    }
}
