package com.example.tasks.security;

import com.example.tasks.config.TasksProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Exige {@code Authorization: Bearer <tasks.api-token>} sur les méthodes en écriture
 * lorsque le token est configuré. Les lectures restent publiques.
 */
@Component
public class ApiTokenInterceptor implements HandlerInterceptor {

    private static final Set<String> READ_METHODS = Set.of("GET", "HEAD", "OPTIONS");

    private final TasksProperties properties;

    public ApiTokenInterceptor(TasksProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!properties.writeProtectionEnabled() || READ_METHODS.contains(request.getMethod())) {
            return true;
        }
        String expected = "Bearer " + properties.apiToken();
        String actual = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (actual != null && MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8))) {
            return true;
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("""
                {"type":"about:blank","title":"Unauthorized","status":401,\
                "detail":"Token manquant ou invalide","instance":"%s"}""".formatted(request.getRequestURI()));
        return false;
    }
}
