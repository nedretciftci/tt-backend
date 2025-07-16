package com.turktelekom.bayi_login.webApi.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turktelekom.bayi_login.business.abstracts.UserService;
import com.turktelekom.bayi_login.business.requests.LoginRequest;
import com.turktelekom.bayi_login.business.responses.LoginResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Login with username and password")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request.getUsername(), request.getPassword())
                .map(user -> {
                    LoginResponse response = new LoginResponse();
                    response.setSuccess(true);
                    response.setMessage("Login successful");
                    response.setRole(user.getRole());
                    return response;
                })
                .orElseGet(() -> {
                    LoginResponse response = new LoginResponse();
                    response.setSuccess(false);
                    response.setMessage("Invalid username or password");
                    return response;
                });
    }
}
