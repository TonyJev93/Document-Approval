package com.tonyjev.documentapproval.domain.document.domain.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprovalStatus {

    WAITING("결재 대기"),
    APPROVING("결재 중"),
    APPROVED("승인"),
    REJECTED("반려"),
    ;

    private String name;    // 승인 상태 명
}
