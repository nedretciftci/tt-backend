package com.turktelekom.bayi_login.business.concretes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.turktelekom.bayi_login.business.abstracts.UserService;
import com.turktelekom.bayi_login.business.requests.ResetPasswordRequest;
import com.turktelekom.bayi_login.business.responses.ForgotPasswordResponse;
import com.turktelekom.bayi_login.business.responses.ResetPasswordResponse;
import com.turktelekom.bayi_login.dataAccess.abstracts.UserRepository;
import com.turktelekom.bayi_login.entities.concretes.User;
import com.turktelekom.bayi_login.core.EmailService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserManager implements UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    public Optional<User> login(String username, String password, boolean rememberMe) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return userOpt;
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public ForgotPasswordResponse forgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return new ForgotPasswordResponse(false, "User with this email does not exist");
        }
        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        String resetLink = "https://your-frontend-app/reset-password?token=" + token;
        emailService.sendEmail(user.getEmail(), "Password Reset Request",
                "Click the link to reset your password: " + resetLink);
        return new ForgotPasswordResponse(true, "Password reset email sent if the email exists in our system.");
    }

    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        Optional<User> userOpt = userRepository.findByResetToken(request.getToken());
        if (userOpt.isEmpty()) {
            return new ResetPasswordResponse(false, "Invalid or expired token.");
        }
        User user = userOpt.get();
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return new ResetPasswordResponse(false, "Token has expired.");
        }
        user.setPassword(request.getNewPassword()); // In production, hash the password!
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        return new ResetPasswordResponse(true, "Password has been reset successfully.");
    }
}
