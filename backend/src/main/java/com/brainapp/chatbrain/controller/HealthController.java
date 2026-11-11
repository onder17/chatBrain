package com.brainapp.chatbrain.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.brainapp.chatbrain.financial.provider.FinancialDataProvider;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {
    private final FinancialDataProvider dataProvider;

    public HealthController(FinancialDataProvider dataProvider) { this.dataProvider = dataProvider; }

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "chatbrain-backend",
                "version", "1.0.0",
                "phase", "Phase 6 - AI-enabled standalone financial assistant",
                "dataProvider", dataProvider.getClass().getSimpleName(),
                "timestamp", Instant.now().toString()
        ));
    }
}
