package com.tonyjev.documentapproval.domain.document.api.dto.request;

import com.tonyjev.documentapproval.domain.document.domain.Approver;
import com.tonyjev.documentapproval.domain.document.domain.Document;
import com.tonyjev.documentapproval.domain.document.domain.code.Classification;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class DocumentCreateRequest {

    @NotEmpty
    private String title;   // 제목

    @NotNull
    private Classification classification;  //분류

    @NotEmpty
    private String content; // 내용

    @NotEmpty
    private List<ApproverDto> approverList; // 결재자 목록

    public Document toEntity() {
        return Document.of(title, classification, content, ApproverDto.toEntityList(approverList));
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    private static class ApproverDto {
        @NotEmpty
        private int seq;

        @NotEmpty
        private String userId;

        public static List<Approver> toEntityList(List<ApproverDto> approverList) {
            return approverList.stream().map(approverDto -> approverDto.toEntity()).collect(Collectors.toList());
        }

        public Approver toEntity() {
            return Approver.builder()
                    .seq(seq)
                    .userId(userId)
                    .build();
        }
    }
}
