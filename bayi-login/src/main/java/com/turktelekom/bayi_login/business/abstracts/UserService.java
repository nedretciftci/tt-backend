package com.turktelekom.bayi_login.business.abstracts;

import java.util.Optional;
import java.util.List;

import com.turktelekom.bayi_login.entities.concretes.User;
import com.turktelekom.bayi_login.business.responses.ForgotPasswordResponse;
import com.turktelekom.bayi_login.business.requests.ResetPasswordRequest;
import com.turktelekom.bayi_login.business.responses.ResetPasswordResponse;

public interface UserService {
    Optional<User> login(String username, String password, boolean rememberMe);

    User save(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    ForgotPasswordResponse forgotPassword(String email);

    ResetPasswordResponse resetPassword(ResetPasswordRequest request);

    List<User> getAllUsers();
}
