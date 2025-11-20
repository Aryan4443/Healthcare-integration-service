package com.healthbridge.integration.api;

import com.healthbridge.integration.api.dto.PatientSnapshotResponse;
import com.healthbridge.integration.application.service.PatientSnapshotService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientSnapshotService patientSnapshotService;

    public PatientController(PatientSnapshotService patientSnapshotService) {
        this.patientSnapshotService = patientSnapshotService;
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientSnapshotResponse> fetchSnapshot(
            @PathVariable @NotBlank String patientId) {
        PatientSnapshotResponse snapshot = patientSnapshotService.fetchSnapshot(UUID.fromString(patientId));
        return ResponseEntity.ok(snapshot);
    }
}

