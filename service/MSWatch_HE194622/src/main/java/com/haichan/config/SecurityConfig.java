package com.haichan.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {

    public static final int ADMIN_ROLE = 1;

    public boolean requiresAuthentication(HttpServletRequest request) {
        return isWriteMethod(request) && !isStockAdjustment(request);
    }

    public boolean requiresAdmin(HttpServletRequest request) {
        return isWriteMethod(request) && !isStockAdjustment(request);
    }

    public boolean isAdmin(Integer role) {
        return role != null && role == ADMIN_ROLE;
    }

    private boolean isWriteMethod(HttpServletRequest request) {
        String method = request.getMethod();
        return HttpMethod.POST.matches(method)
                || HttpMethod.PUT.matches(method)
                || HttpMethod.DELETE.matches(method);
    }

    /** Saga/Order will call these later; do not require admin JWT. */
    private boolean isStockAdjustment(HttpServletRequest request) {
        return HttpMethod.POST.matches(request.getMethod())
                && request.getRequestURI().matches(".*/api/v1/watches/\\d+/(reserve|restore)");
    }
}
