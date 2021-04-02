package com.tonyjev.documentapproval.domain.document.domain;

import com.tonyjev.documentapproval.domain.document.domain.code.ApprovalStatus;
import com.tonyjev.documentapproval.domain.document.domain.code.Classification;
import com.tonyjev.documentapproval.global.audit.AuditEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.tonyjev.documentapproval.domain.document.domain.code.ApprovalStatus.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Document extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)    // Enum 상수값 DB 저장 (성능상 좋지는 않음)
    @Column(nullable = false)
    private Classification classification;

    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = APPROVING;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "documentId")
    private List<Approver> approverList = new ArrayList<>();


    @Builder
    private Document(String title, Classification classification, String content, List<Approver> approverList) {
        this.title = title;
        this.classification = classification;
        this.content = content;
        this.approverList = approverList;
    }

    public static Document of(String title, Classification classification, String content, List<Approver> approverList) {
        return Document.builder()
                .title(title)
                .classification(classification)
                .content(content)
                .approverList(approverList)
                .build();
    }

    public void approved() {
        this.approvalStatus = APPROVED;
    }

    public void changeApprovalStatus(List<Approver> approverList) {
        if (approverList.stream().anyMatch(approver -> approver.getApprovalStatus().equals(REJECTED))) {
            // 결재자 중 반려가 존재하는 경우
            this.rejected(); // 반려
        } else if (approverList.stream().allMatch(approver -> approver.getApprovalStatus().equals(APPROVED))) {
            // 결재자 모두 승인한 경우
            this.approved(); // 승인 완료
        }
    }

    private void approving() {
        this.approvalStatus = APPROVING;
    }

    private void rejected() {
        this.approvalStatus = REJECTED;
    }
}
