package com.winestoreapp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.MalformedURLException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST,
        RequestMethod.PATCH, RequestMethod.DELETE})
@Tag(name = "Health check management",
        description = "Endpoint health check controller")
public class HealthCheckController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> healthCheck() throws MalformedURLException {
        return ResponseEntity.ok("Health check passed. "
                + "Application is running smoothly.");
    }
}
