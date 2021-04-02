package com.tonyjev.documentapproval.domain.document.application.validator;

import com.tonyjev.documentapproval.domain.document.application.exception.*;
import com.tonyjev.documentapproval.domain.document.domain.Approver;
import com.tonyjev.documentapproval.domain.document.domain.Document;
import com.tonyjev.documentapproval.domain.user.domain.User;

import java.util.List;
import java.util.stream.Collectors;

import static com.tonyjev.documentapproval.domain.document.domain.code.ApprovalStatus.*;

public class DocumentValidator {

    // 승인 관련 유효성 검증
    public static void validateApproval(Document document, List<Approver> approverList, String userId) {
        // 문서가 이미 결재 or 반려된 경우
        if (document.getApprovalStatus().equals(APPROVED)
                || document.getApprovalStatus().equals(REJECTED)) {
            throw new DocumentApprovalNotPossibleException();
        }

        // 결재 리스트에 사용자가 존재하지 않는 경우
        if (approverList.stream().noneMatch(approver -> approver.isEqualsUserId(userId))) {
            throw new ApprovalAuthorizationNotHaveException();
        }

        // 이미 결재를 진행한 경우
        if (approverList.stream()
                .filter(approver -> approver.isEqualsUserId(userId))
                .allMatch(approver -> approver.isApprovalYn())) {
            throw new AlreadyApprovedApproverException();
        }

        // 본인이 결재할 차례가 아닌 경우
        if (approverList.stream()
                .filter(approver -> approver.isEqualsUserId(userId))
                .noneMatch(approver -> approver.getApprovalStatus().equals(APPROVING))) {
            throw new ApprovalTurnNotMineException();
        }
    }


    public static void validateCreate(List<Approver> approverList, List<User> joinUserList) {
        // 결재자 목록 검증
        if (!joinUserList.stream().map(User::getUserId).collect(Collectors.toSet())
                .equals(approverList.stream().map(Approver::getUserId).collect(Collectors.toSet()))) {
            // 결재자 목록 회원 여부 확인
            throw new ApproverNotFoundException();
        }
    }
}
