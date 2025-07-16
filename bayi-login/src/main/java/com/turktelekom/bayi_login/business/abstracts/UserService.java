package com.turktelekom.bayi_login.business.abstracts;

import java.util.Optional;

import com.turktelekom.bayi_login.entities.concretes.User;

public interface UserService {
    Optional<User> login(String username, String password);

    User save(User user);

    Optional<User> findByUsername(String username);
}
