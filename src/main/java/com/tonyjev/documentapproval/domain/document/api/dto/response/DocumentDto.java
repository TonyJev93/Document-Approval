package com.tonyjev.documentapproval.domain.document.api.dto.response;

import com.tonyjev.documentapproval.domain.document.domain.Approver;
import com.tonyjev.documentapproval.domain.document.domain.Document;
import com.tonyjev.documentapproval.domain.document.domain.code.ApprovalStatus;
import com.tonyjev.documentapproval.domain.document.domain.code.Classification;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder(access = AccessLevel.PROTECTED)
public class DocumentDto {
    private Long documentId;
    private String title;
    private Classification classification;
    private String content;
    private ApprovalStatus approvalStatus;
    private String createUserId;
    private LocalDateTime createdAt;
    private String updateUserId;
    private LocalDateTime updatedAt;
    private List<ApproverDto> approverList;

    public static DocumentDto of(Document document) {
        return DocumentDto.builder()
                .documentId(document.getId())
                .title(document.getTitle())
                .classification(document.getClassification())
                .content(document.getContent())
                .approvalStatus(document.getApprovalStatus())
                .createUserId(document.getCreateUserId())
                .createdAt(document.getCreatedAt())
                .updateUserId(document.getUpdateUserId())
                .updatedAt(document.getUpdatedAt())
                .approverList(ApproverDto.ofList(document.getApproverList()))
                .build();
    }

    @Getter
    @Builder
    private static class ApproverDto {
        private int seq; // 결재 순번
        private String userId; // 사용자 ID
        private boolean approvalYn; // 결재 여부
        private ApprovalStatus approvalStatus; // 결재 상태
        private String comment; // 첨언

        public static List<ApproverDto> ofList(List<Approver> approverList) {
            return approverList.stream().map(approver -> ApproverDto.of(approver)).collect(Collectors.toList());
        }

        private static ApproverDto of(Approver approver) {
            return ApproverDto.builder()
                    .seq(approver.getSeq())
                    .userId(approver.getUserId())
                    .approvalYn(approver.isApprovalYn())
                    .approvalStatus(approver.getApprovalStatus())
                    .comment(approver.getComment())
                    .build();
        }
    }
}
