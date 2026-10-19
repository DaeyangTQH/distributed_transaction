package com.haichan.config;

import com.haichan.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtFilter extends OncePerRequestFilter {

    private static final int ADMIN_ROLE = 1;
    private final JwtService jwtService;
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (HttpMethod.GET.matches(method) && path.startsWith("/api/v1/watches")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!path.startsWith("/api/v1/watches")
                || !(HttpMethod.POST.matches(method)
                || HttpMethod.PUT.matches(method)
                || HttpMethod.DELETE.matches(method))) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }
        String token = authHeader.substring(7);
        try {
            if (!jwtService.isValid(token)) {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                return;
            }
            Integer role = jwtService.getRole(token);
            if (role == null || role != ADMIN_ROLE) {
                writeError(response, HttpServletResponse.SC_FORBIDDEN, "Administrator role required");
                return;
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        }
    }
    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\"}");
    }
}
