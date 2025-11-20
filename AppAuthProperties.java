package com.healthbridge.integration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.auth")
public record AppAuthProperties(String apiKey) {
}

