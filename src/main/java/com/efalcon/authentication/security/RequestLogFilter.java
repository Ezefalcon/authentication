package com.efalcon.authentication.security;

import com.efalcon.authentication.model.RequestLog;
import com.efalcon.authentication.repository.RequestLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLogFilter extends OncePerRequestFilter {

    private final RequestLogRepository requestLogRepository;

    private static final List<String> SENSITIVE_KEYS = List.of(
        "password", "token", "authorization", "secret", "ssn"
    );

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
            ServletException, IOException {
        // Note: Reading the request body directly here can cause issues for downstream filters/controllers.
        // Use ContentCachingRequestWrapper to avoid consuming the body prematurely.
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        filterChain.doFilter(wrappedRequest, response);

        String requestURI = request.getRequestURI();
        String body = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
        String requestBody = maskSensitiveJson(body);
        Map<String, String> headers = getHeadersMap(request);
        String ipOfOrigin = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String requestParams = getParams(wrappedRequest);
        String requestId = UUID.randomUUID().toString();
        RequestLog requestLog = RequestLog
                .builder()
                .requestIp(ipOfOrigin)
                .request(requestBody)
                .headers(writeHeadersAsString(headers))
                .creationDate(LocalDateTime.now())
                .requestUri(requestURI)
                .userAgent(userAgent)
                .params(requestParams)
                .requestId(requestId)
                .build();
        requestLogRepository.save(requestLog);
        log.debug("Saved incoming request to {}.", requestURI);
    }

    private String writeHeadersAsString(Map<String, String> headers) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(headers);
        } catch (Exception ex) {
            log.error("Failed to parse headers: {}.", ex.getMessage(), ex);
        }
        return null;
    }

    private static Map<String, String> getHeadersMap(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }

    private static String getParams(ContentCachingRequestWrapper wrappedRequest) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(wrappedRequest.getParameterMap());
    }

    private String maskSensitiveJson(String body) {
        try {
            JsonNode root = mapper.readTree(body);
            maskNode(root);
            return mapper.writeValueAsString(root);
        } catch (Exception e) {
            return body;
        }
    }

    private void maskNode(JsonNode node) {
        if (node.isObject()) {
            node.fieldNames().forEachRemaining(field -> {
                if (SENSITIVE_KEYS.contains(field.toLowerCase())) {
                    ((com.fasterxml.jackson.databind.node.ObjectNode) node).put(field, "[MASKED]");
                } else {
                    maskNode(node.get(field));
                }
            });
        } else if (node.isArray()) {
            node.forEach(this::maskNode);
        }
    }
}

