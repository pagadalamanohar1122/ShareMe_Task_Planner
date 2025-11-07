package com.tasksphere.shareme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tasksphere.shareme.dto.AuthResponse;
import com.tasksphere.shareme.dto.ErrorResponse;
import com.tasksphere.shareme.dto.ForgotPasswordRequest;
import com.tasksphere.shareme.dto.LoginRequest;
import com.tasksphere.shareme.dto.ResetPasswordRequest;
import com.tasksphere.shareme.dto.SignupRequest;
import com.tasksphere.shareme.dto.UserInfo;
import com.tasksphere.shareme.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and user management APIs - Register, login, password reset, and user profile management")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Create a new user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "409", description = "Email already registered",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(201).build();
    }
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get current authenticated user information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User information retrieved",
                content = @Content(schema = @Schema(implementation = UserInfo.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserInfo> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserInfo userInfo = authService.getCurrentUser(email);
        return ResponseEntity.ok(userInfo);
    }
    
    @PostMapping("/forgot")
    @Operation(summary = "Forgot password", description = "Request password reset token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Password reset token sent"),
        @ApiResponse(responseCode = "400", description = "Invalid input",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/reset")
    @Operation(summary = "Reset password", description = "Reset password using token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Password reset successful"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired token",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
}