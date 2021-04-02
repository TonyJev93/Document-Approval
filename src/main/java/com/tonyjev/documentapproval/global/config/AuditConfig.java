package com.tonyjev.documentapproval.global.config;

import com.tonyjev.documentapproval.global.security.utils.SecurityContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class AuditConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(SecurityContextUtil.getLoginUserId());
    }
}
