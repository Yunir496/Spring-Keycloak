package com.example.keycloakdemo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {
    private static final Logger log = LoggerFactory.getLogger(DemoController.class);
    @GetMapping("/admin/ping")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin() {
        log.info("ADMIN endpoint ping");
        return "hello ADMIN";
    }
    @GetMapping("/user/ping")
    @PreAuthorize("hasRole('USER')")
    public String user() {
        log.info("USER endpoint ping");
        return "hello USER";
    }
    @GetMapping("/moderator/ping")
    @PreAuthorize("hasRole('MODERATOR')")
    public String moderator() {
        log.info("MODERATOR endpoint ping");
        return "hello MODERATOR";
    }
}
