package com.healthbridge.integration.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PatientSnapshotResponse(
        UUID patientId,
        Demographics demographics,
        List<EncounterSummary> encounters,
        List<ObservationSummary> recentObservations
) {

    public record Demographics(
            String mrn,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            String gender,
            String facilityCode,
            String consentStatus
    ) {
    }

    public record EncounterSummary(
            UUID encounterId,
            String externalEncounterId,
            String encounterType,
            Instant start,
            Instant end,
            String facilityCode
    ) {
    }

    public record ObservationSummary(
            UUID observationId,
            String code,
            String system,
            String display,
            BigDecimal value,
            String unit,
            String status,
            Instant effectiveDateTime,
            UUID encounterId
    ) {
    }
}

