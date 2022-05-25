package com.st.authlight.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    private static final HealthCheckDto UP = new HealthCheckDto("UP");

    @GetMapping("management/health")
    public HealthCheckDto healthCheck() {
        return UP;
    }

    @Data
    @AllArgsConstructor
    public static class HealthCheckDto {
        private String status;
    }
}