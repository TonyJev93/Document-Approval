package com.tonyjev.documentapproval.domain.document.domain;

import com.tonyjev.documentapproval.domain.document.domain.code.ApprovalStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ApproverPk.class)
public class Approver {

    @Id
    private Long documentId; // 문서 id

    @Id
    private int seq; // 결재 순번

    @Column(nullable = false)
    private String userId; // 사용자 ID

    @Column(nullable = false)
    private boolean approvalYn = false; // 결재 여부

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus = ApprovalStatus.WAITING; // 결재 상태

    private String comment = ""; // 첨언

    @Builder
    private Approver(int seq, String userId) {
        this.seq = seq;
        this.userId = userId;

        // 첫 번째 순번의 결재자 결재 상태 : 결재중
        if (seq == 1) {
            this.approvalStatus = ApprovalStatus.APPROVING;
        }
    }

    public static Approver of(int seq, String userId) {
        return Approver.builder()
                .seq(seq)
                .userId(userId)
                .build();
    }

    public static List<Long> extractDocumentIdList(List<Approver> myApprovalList) {
        return myApprovalList.stream().map(approver -> approver.getDocumentId()).collect(Collectors.toList());
    }

    public static void updateNextApproverStatus(List<Approver> approverList) {
        // 결재순(Seq 순)으로 정렬
        approverList.stream().sorted(Comparator.comparing(Approver::getSeq));

        // 결재 대기중인 첫 번째 결재자의 상태를 '결재 중'으로 변경
        for (Approver approver : approverList) {
            if (approver.getApprovalStatus().equals(ApprovalStatus.WAITING)) {
                approver.updateApprovalStatus(ApprovalStatus.APPROVING);
                return;
            }
        }
    }

    public static Optional<Approver> extractCurrentApprover(List<Approver> approverList, String userId) {
        // 결재 목록중 이름 일치 + 결재 여부 N
        return approverList.stream().filter(approver -> approver.getUserId().equals(userId) && !approver.isApprovalYn()).findFirst();
    }

    public void updateApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public void updateDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    // 승인 처리
    public void approve(String comment) {
        // 상태 : 결재 중 -> 승인
        this.approvalStatus = ApprovalStatus.APPROVED;

        // 결재 여부 : N -> Y
        this.processApproval();

        // 첨언 추가
        this.addComment(comment);
    }

    // 반려 처리
    public void reject(String comment) {
        // 상태 : 결재 중 -> 승인
        this.approvalStatus = ApprovalStatus.REJECTED;

        // 결재 여부 : N -> Y
        this.processApproval();

        // 첨언 추가
        this.addComment(comment);
    }

    public void addComment(String comment) {
        this.comment = comment;
    }

    public void processApproval() {
        this.approvalYn = true;
    }

    public boolean isEqualsUserId(String userId) {
        return this.userId.equals(userId);
    }

    public static List<String> getUserIdList(List<Approver> approverList) {
        return approverList.stream().map(approver -> approver.getUserId()).collect(Collectors.toList());
    }
}
