package com.healthbridge.integration.api;

import com.healthbridge.integration.api.dto.ObservationIngestRequest;
import com.healthbridge.integration.api.dto.ObservationIngestResponse;
import com.healthbridge.integration.application.service.ObservationIngestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ingest")
public class ObservationIngestionController {

    private final ObservationIngestionService ingestionService;

    public ObservationIngestionController(ObservationIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/observations")
    public ResponseEntity<ObservationIngestResponse> ingest(@Valid @RequestBody ObservationIngestRequest request) {
        ObservationIngestResponse response = ingestionService.ingest(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}

