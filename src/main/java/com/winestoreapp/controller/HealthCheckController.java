package com.winestoreapp.controller;

import com.winestoreapp.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
@Tag(name = "Health check management",
        description = "Endpoint health check controller")
public class HealthCheckController {
    private final UserRepository userRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> healthCheck() {
        log.info("Users quantity: " + userRepository.count());
        return ResponseEntity.ok("Health check passed. "
                + "Application is running smoothly.");
    }
}
