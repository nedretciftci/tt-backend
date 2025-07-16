package com.turktelekom.bayi_login.webApi.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turktelekom.bayi_login.business.abstracts.UserService;
import com.turktelekom.bayi_login.business.requests.LoginRequest;
import com.turktelekom.bayi_login.business.responses.LoginResponse;
import com.turktelekom.bayi_login.business.requests.ForgotPasswordRequest;
import com.turktelekom.bayi_login.business.responses.ForgotPasswordResponse;
import com.turktelekom.bayi_login.business.requests.ResetPasswordRequest;
import com.turktelekom.bayi_login.business.responses.ResetPasswordResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Login with username and password")
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return userService.login(request.getUsername(), request.getPassword(), request.isRememberMe())
                .map(user -> {
                    if (request.isRememberMe()) {
                        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("remember-me",
                                user.getUsername());
                        cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
                        cookie.setPath("/");
                        cookie.setHttpOnly(true);
                        response.addCookie(cookie);
                    }
                    LoginResponse loginResponse = new LoginResponse();
                    loginResponse.setSuccess(true);
                    loginResponse.setMessage("Login successful");
                    loginResponse.setRole(user.getRole());
                    return loginResponse;
                })
                .orElseGet(() -> {
                    LoginResponse loginResponse = new LoginResponse();
                    loginResponse.setSuccess(false);
                    loginResponse.setMessage("Invalid username or password");
                    return loginResponse;
                });
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset by email")
    public ForgotPasswordResponse forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return userService.forgotPassword(request.getEmail());
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with token and new password")
    public ResetPasswordResponse resetPassword(@RequestBody ResetPasswordRequest request) {
        return userService.resetPassword(request);
    }
}
