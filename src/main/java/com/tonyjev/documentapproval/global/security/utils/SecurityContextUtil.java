package com.tonyjev.documentapproval.global.security.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextUtil {
    public static String getLoginUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
