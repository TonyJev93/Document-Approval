package com.tonyjev.documentapproval.global.audit;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
@Getter
public class AuditEntity extends AuditTimeEntity {
    @CreatedBy
    @Column(updatable = false)
    private String createUserId;

    @LastModifiedBy
    private String updateUserId;
}
