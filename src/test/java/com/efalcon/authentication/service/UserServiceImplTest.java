package com.efalcon.authentication.service;

import com.efalcon.authentication.model.User;
import com.efalcon.authentication.model.dto.TokenDTO;
import com.efalcon.authentication.model.dto.UserLogin;
import com.efalcon.authentication.repository.UserRepository;
import com.efalcon.authentication.service.exceptions.UserNotFoundException;
import com.efalcon.authentication.service.exceptions.UsernameAlreadyExistsException;
import com.efalcon.authentication.service.exceptions.UsernameOrPasswordInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
    }

    @Test
    void save_ShouldSaveUser_WhenUsernameDoesNotExist() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.save(user);
        assertNotNull(savedUser);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void save_ShouldThrowException_WhenUsernameExists() {
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.save(user));
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        User foundUser = userService.findByUsernameOrErr(user.getUsername());
        assertNotNull(foundUser);
        assertEquals(user.getUsername(), foundUser.getUsername());
    }

    @Test
    void findByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.findByUsernameOrErr(user.getUsername()));
    }

    @Test
    void login_ShouldReturnTokenDTO_WhenCredentialsAreValid() {
        UserLogin userLogin = new UserLogin("testuser", "password");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userLogin.getPassword(), user.getPassword())).thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn("validToken");

        TokenDTO tokenDTO = userService.login(userLogin);
        assertNotNull(tokenDTO);
        assertEquals("validToken", tokenDTO.getToken());
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsAreInvalid() {
        UserLogin userLogin = new UserLogin("testuser", "wrongPassword");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userLogin.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(UsernameOrPasswordInvalidException.class, () -> userService.login(userLogin));
    }

    @Test
    void removeById_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        doNothing().when(userRepository).deleteById(user.getId());

        assertDoesNotThrow(() -> userService.removeById(user.getId()));
        verify(userRepository).deleteById(user.getId());
    }

    @Test
    void removeById_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.existsById(user.getId())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.removeById(user.getId()));
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());
        assertNotNull(userDetails);
        assertEquals(user.getUsername(), userDetails.getUsername());
    }
}
