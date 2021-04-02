package com.tonyjev.documentapproval.domain.document.api.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
public class DocumentRejectRequest {

    @NotEmpty
    private String comment;   // 첨언

}
