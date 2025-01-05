package com.efalcon.authentication.service;

import com.efalcon.authentication.model.User;
import com.efalcon.authentication.model.dto.TokenDTO;
import com.efalcon.authentication.model.dto.UserLogin;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {
    Optional<User> findById(Long id);
    User save(User user);
    User update(String id, User user);
    void removeById(Long id);
    User findByUsername(String username);
    boolean existsByUsername(String username);

    TokenDTO login(UserLogin userLogin);
}
