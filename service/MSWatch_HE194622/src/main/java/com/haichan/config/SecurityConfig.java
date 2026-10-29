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
        if (!HttpMethod.POST.matches(request.getMethod())) {
            return false;
        }
        String path = request.getRequestURI();
        return path.matches(".*/api/v1/watches/\\d+/(reserve|restore)")
                || path.matches(".*/api/v1/watches/(reserve-batch|restore-batch)");
    }
}
