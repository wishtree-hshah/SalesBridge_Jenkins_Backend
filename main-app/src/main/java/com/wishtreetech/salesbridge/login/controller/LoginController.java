package com.wishtreetech.salesbridge.login.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Login API", description = "Endpoints for managing login")

public class LoginController {

    /**
     * Redirects on the OAuth login page.
     * @return
     */
    @GetMapping("login")
    @Operation(summary = "login user", description = "login")

    public ResponseEntity<Void> login(){
        return ResponseEntity.status(302).header("Location", "/oauth2/authorization/google").build();
    }
}
