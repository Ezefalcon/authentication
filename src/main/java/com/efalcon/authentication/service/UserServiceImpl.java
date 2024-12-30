package com.efalcon.authentication.service;

import com.efalcon.authentication.model.Provider;
import com.efalcon.authentication.model.User;
import com.efalcon.authentication.model.dto.TokenDTO;
import com.efalcon.authentication.model.dto.UserLogin;
import com.efalcon.authentication.repository.UserRepository;
import com.efalcon.authentication.service.exceptions.UserNotFoundException;
import com.efalcon.authentication.service.exceptions.UsernameAlreadyExistsException;
import com.efalcon.authentication.service.exceptions.UsernameOrPasswordInvalidException;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;


/**
 * Created by efalcon
 */
@NoArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private TokenService tokenService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
        if(!userRepository.existsByUsername(user.getUsername())) {
            String encodedPassword = this.passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            return this.userRepository.save(user);
        }
        throw new UsernameAlreadyExistsException();
    }

    @Override
    public User update(String id, User user) {
        return null;
    }

    @Override
    public void removeById(String id) {
        if(this.userRepository.existsById(id)) {
            this.userRepository.deleteById(id);
        } else throw new UserNotFoundException();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public TokenDTO login(@Valid UserLogin userLogin) {
        User user = findByUsername(userLogin.getUsername());
        if(Objects.nonNull(user) && passwordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
            String token = tokenService.generateToken(user, Provider.LOCAL);
            return new TokenDTO(token);
        }
        throw new UsernameOrPasswordInvalidException();
    }

    @Override
    public UserDetails loadUserByUsername(String username){
        User applicationUser = userRepository.findByUsername(username);
        if (Objects.isNull(applicationUser)) {
            throw new UsernameNotFoundException(username);
        }
        return new org.springframework.security.core.userdetails.User(applicationUser.getUsername(), applicationUser.getPassword(), emptyList());
    }
}
