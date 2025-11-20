package com.healthbridge.integration.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "X-API-Key";

    private final AppAuthProperties authProperties;
    private final List<RequestMatcher> excludedMatchers = List.of(
            new AntPathRequestMatcher("/actuator/**"),
            new AntPathRequestMatcher("/v3/api-docs/**"),
            new AntPathRequestMatcher("/swagger-ui/**"),
            new AntPathRequestMatcher("/swagger-ui.html")
    );

    public ApiKeyAuthenticationFilter(AppAuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return excludedMatchers.stream().anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String expectedApiKey = authProperties.apiKey();
        if (expectedApiKey == null || expectedApiKey.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        String presentedKey = request.getHeader(HEADER_NAME);
        if (!expectedApiKey.equals(presentedKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid API key");
            return;
        }

        var authentication = new UsernamePasswordAuthenticationToken(
                "api-client",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_SYSTEM"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}

