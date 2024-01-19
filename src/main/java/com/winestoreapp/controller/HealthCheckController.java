package com.winestoreapp.controller;

import com.winestoreapp.model.Role;
import com.winestoreapp.repository.RoleRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
@Tag(name = "Health check management",
        description = "Endpoint health check controller")
public class HealthCheckController {
    private final RoleRepository roleRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> healthCheck() {
        final List<Role> all = roleRepository.findAll();
        System.out.println(all.isEmpty());
        return ResponseEntity.ok("Health check passed. "
                + "Application is running smoothly.");
    }
}
