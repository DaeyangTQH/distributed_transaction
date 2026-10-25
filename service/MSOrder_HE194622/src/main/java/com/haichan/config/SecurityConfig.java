package com.haichan.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class SecurityConfig {

    public static final int ADMIN_ROLE = 1;
    private static final String ORDERS_PATH = "/api/v1/orders";

    public boolean requiresAuthentication(HttpServletRequest request) {
        return request.getRequestURI().startsWith(ORDERS_PATH);
    }

    /** GET /api/v1/orders (list) is admin-only. GET /api/v1/orders/{id} is owner-checked in service. */
    public boolean requiresAdmin(HttpServletRequest request) {
        return HttpMethod.GET.matches(request.getMethod())
                && isOrdersCollection(request.getRequestURI());
    }

    public boolean isAdmin(Integer role) {
        return role != null && role == ADMIN_ROLE;
    }

    private boolean isOrdersCollection(String path) {
        String normalized = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        return ORDERS_PATH.equals(normalized);
    }
}
