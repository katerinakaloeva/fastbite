package com.fastbite.identityservice.identity.controller;

import com.fastbite.identityservice.identity.dto.LoginRequest;
import com.fastbite.identityservice.identity.dto.LoginResponse;
import com.fastbite.identityservice.identity.dto.RegisterRequest;
import com.fastbite.identityservice.identity.dto.UserResponse;
import com.fastbite.identityservice.identity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Authentication",
        description = "Operations for registering and authenticating users."
)
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(
            summary = "Register a customer",
            description = "Creates a customer account and returns the public user details."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid registration data."),
            @ApiResponse(responseCode = "409", description = "Email is already registered.")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Log in",
            description = "Checks the email and password and returns a JWT access token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, token returned."),
            @ApiResponse(responseCode = "400", description = "Email or password is missing."),
            @ApiResponse(responseCode = "401", description = "Invalid email or password.")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);

        return ResponseEntity.ok(response);
    }
}
