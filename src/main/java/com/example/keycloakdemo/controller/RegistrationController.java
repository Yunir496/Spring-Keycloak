package com.example.keycloakdemo.controller;

import com.example.keycloakdemo.service.KeycloakAdminService;
import com.example.keycloakdemo.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class RegistrationController {
    private final KeycloakAdminService service;
    public RegistrationController(KeycloakAdminService service) {
        this.service = service;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        service.registerUser(req);
        return ResponseEntity.ok().build();
    }
}
