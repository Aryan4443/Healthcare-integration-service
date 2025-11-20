package com.healthbridge.integration.api.dto;

import java.util.UUID;

public record ObservationIngestResponse(UUID ingestionId, String status) {
}

