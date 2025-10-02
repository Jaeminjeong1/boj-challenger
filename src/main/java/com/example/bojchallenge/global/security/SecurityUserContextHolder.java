package com.example.bojchallenge.global.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUserContextHolder {

    private SecurityUserContextHolder() {
    }

    public static SecurityUserContext getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUserContext context)) {
            return null;
        }
        return context;
    }

    public static Long getCurrentUserId() {
        SecurityUserContext context = getCurrentUser();
        return context != null ? context.userId() : null;
    }
}
