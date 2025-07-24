package com.turktelekom.bayi_login.webApi.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;
import com.turktelekom.bayi_login.entities.concretes.UserRole;

import com.turktelekom.bayi_login.business.abstracts.UserService;
import com.turktelekom.bayi_login.business.requests.LoginRequest;
import com.turktelekom.bayi_login.business.responses.LoginResponse;
import com.turktelekom.bayi_login.business.requests.ForgotPasswordRequest;
import com.turktelekom.bayi_login.business.responses.ForgotPasswordResponse;
import com.turktelekom.bayi_login.business.requests.ResetPasswordRequest;
import com.turktelekom.bayi_login.business.responses.ResetPasswordResponse;
import com.turktelekom.bayi_login.core.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletResponse response) {
        return userService.login(request.getEmail(), request.getPassword(), request.isRememberMe())
                .map(user -> {
                    if (request.isRememberMe()) {
                        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("remember-me",
                                user.getEmail());
                        cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
                        cookie.setPath("/");
                        cookie.setHttpOnly(true);
                        response.addCookie(cookie);
                    }
                    LoginResponse loginResponse = new LoginResponse();
                    loginResponse.setSuccess(true);
                    loginResponse.setMessage("Login successful");
                    loginResponse.setRole(user.getRole());
                    loginResponse.setUsername(user.getUsername());
                    String token = jwtUtil.generateToken(user.getEmail());
                    loginResponse.setToken(token);
                    return loginResponse;
                })
                .orElseGet(() -> {
                    LoginResponse loginResponse = new LoginResponse();
                    loginResponse.setSuccess(false);
                    loginResponse.setMessage("Invalid email or password");
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

    @PostMapping("/logout")
    @Operation(summary = "Logout and clear remember-me cookie")
    public void logout(HttpServletResponse response) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("remember-me", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    @GetMapping("/profile/{username}")
    @SecurityRequirement(name = "bearerAuth")
    public com.turktelekom.bayi_login.entities.concretes.User getProfile(@PathVariable String username) {
        return userService.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @GetMapping("/users")
    @SecurityRequirement(name = "bearerAuth")
    public List<com.turktelekom.bayi_login.entities.concretes.User> getAllUsers(
            @RequestHeader("Authorization") String authHeader) {
        // JWT'den rolü çözümle
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);
        com.turktelekom.bayi_login.entities.concretes.User user = userService.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (user.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admin can access this endpoint");
        }
        return userService.getAllUsers();
    }
}
