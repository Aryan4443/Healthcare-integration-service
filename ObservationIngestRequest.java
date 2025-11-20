package com.healthbridge.integration.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record ObservationIngestRequest(
        @NotBlank(message = "sourceSystem is required")
        String sourceSystem,
        @NotNull(message = "patient context is required")
        @Valid
        PatientContext patient,
        @Valid
        EncounterContext encounter,
        @NotEmpty(message = "at least one observation is required")
        @Size(max = 1000, message = "observation batch too large")
        @Valid
        List<ObservationPayload> observations
) {

    public record PatientContext(
            @NotBlank String mrn,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotNull LocalDate dateOfBirth,
            @NotBlank String gender,
            boolean consented,
            String facilityCode,
            String phone,
            String email
    ) {
    }

    public record EncounterContext(
            String encounterId,
            String type,
            Instant start,
            Instant end,
            String facilityCode
    ) {
    }

    public record ObservationPayload(
            @NotBlank String code,
            String system,
            String display,
            @NotNull BigDecimal value,
            String unit,
            String status,
            @NotNull Instant effectiveDateTime
    ) {
    }
}

