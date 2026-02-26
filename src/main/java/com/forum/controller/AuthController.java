package com.forum.controller;

import com.forum.dto.AuthResponse;
import com.forum.dto.LoginRequest;
import com.forum.dto.RegisterRequest;
import com.forum.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account and receive JWT token")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "409", description = "Email or display name already exists")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and receive JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid email or password")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

//    @PostMapping("/validate-token")
//    @Operation(summary = "Validate token", description = "Validate if JWT token is valid")
//    @ApiResponse(responseCode = "200", description = "Token is valid")
//    @ApiResponse(responseCode = "401", description = "Token is invalid")
//    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String token) {
//        if (token.startsWith("Bearer ")) {
//            token = token.substring(7);
//        }
//        return ResponseEntity.ok("Token is valid");
//    }
}
