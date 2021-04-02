package com.tonyjev.documentapproval.domain.document.domain.code;

import lombok.Getter;

@Getter
public enum RetrieveType {
    outbox,
    inbox,
    archive,
    ;
}
