package com.turktelekom.bayi_login.business.responses;

import com.turktelekom.bayi_login.entities.concretes.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private String message;
    private UserRole role;
    private String token;
    private String username;

    public boolean isSuccess() {
        return success;
    }
}
