package com.tonyjev.documentapproval.domain.document.domain.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Classification {

    VACATION("휴가신청"),
    REPORT("보고"),
    NOTIFICATION("공지"),
    ;

    private String name;

}
