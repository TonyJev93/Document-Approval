package com.tonyjev.documentapproval.domain.document.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ApproverPk implements Serializable {

    private Long documentId;
    private int seq;

    @Builder
    protected ApproverPk(Long documentId, int seq) {
        this.documentId = documentId;
        this.seq = seq;
    }

    public static ApproverPk of(Long documentId, int seq) {
        return ApproverPk.builder()
                .documentId(documentId)
                .seq(seq)
                .build();
    }
}
