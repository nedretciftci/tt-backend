package com.turktelekom.bayi_login.business.concretes;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.turktelekom.bayi_login.business.abstracts.UserService;
import com.turktelekom.bayi_login.dataAccess.abstracts.UserRepository;
import com.turktelekom.bayi_login.entities.concretes.User;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserManager implements UserService {
    private final UserRepository userRepository;

    @Override
    public Optional<User> login(String username, String password) {
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
}
