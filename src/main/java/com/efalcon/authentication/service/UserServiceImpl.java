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
    public Optional<User> findById(Long id) {
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
    public User update(Long id, User user) {
        Optional<User> byId = userRepository.findById(id);

        if (byId.isEmpty()) throw new UserNotFoundException();

        user.setId(id);
        return this.save(user);
    }

    @Override
    public void removeById(Long id) {
        if(this.userRepository.existsById(id)) {
            this.userRepository.deleteById(id);
        } else throw new UserNotFoundException();
    }

    @Override
    public User findByUsername(String username) throws UserNotFoundException {
        Optional<User> byUsername = userRepository.findByUsername(username);
        return byUsername.orElseThrow(UserNotFoundException::new);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public TokenDTO login(@Valid UserLogin userLogin) {
        try {
            User user = findByUsername(userLogin.getUsername());
            if(Objects.nonNull(user) && passwordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
                String token = tokenService.generateToken(user);
                return new TokenDTO(token);
            }
        } catch (UsernameNotFoundException e) {
            throw new UsernameOrPasswordInvalidException();
        }
        throw new UsernameOrPasswordInvalidException();
    }

    @Override
    public UserDetails loadUserByUsername(String username){
        User applicationUser = this.findByUsername(username);
        return new org.springframework.security.core.userdetails.User(applicationUser.getUsername(), applicationUser.getPassword(), emptyList());
    }
}
