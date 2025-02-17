package com.efalcon.authentication.controller;

import com.efalcon.authentication.model.User;
import com.efalcon.authentication.model.dto.TokenDTO;
import com.efalcon.authentication.model.dto.UserDto;
import com.efalcon.authentication.model.dto.UserLogin;
import com.efalcon.authentication.model.dto.UserRegister;
import com.efalcon.authentication.service.UserService;
import com.efalcon.authentication.test.config.SimpleIntegrationTestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class UserControllerTestIT extends SimpleIntegrationTestConfig {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @MockitoBean
    private UserService userService;

    @Autowired
    private UserController userController;

    @Test
    void testRegisterUser() {
        // Arrange
        UserRegister userRegister = new UserRegister();
        userRegister.setUsername("testUser");
        userRegister.setPassword("password123");

        User updatedUser = new User();
        updatedUser.setUsername("testUser");

        when(userService.save(any())).thenReturn(updatedUser);

        String url = this.getUrl() + "/users";

        // Act
        ResponseEntity<UserDto> response = this.restTemplate.postForEntity(url, userRegister, UserDto.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testUser", response.getBody().getUsername());
    }

    @Test
    void testFindById() {
        // Arrange
        long userId = 1L;

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testUser");

        when(userService.findById(userId)).thenReturn(Optional.of(mockUser));

        String url = this.getUrl() + "/users/" + userId;

        // Act
        ResponseEntity<UserDto> response = restTemplate.getForEntity(url, UserDto.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testUser", response.getBody().getUsername());
    }

    @Test
    void testLogin() {
        // Arrange
        UserLogin userLogin = new UserLogin();
        userLogin.setUsername("testUser");
        userLogin.setPassword("password123");

        String url = this.getUrl() + "/users/login";

        TokenDTO mockToken = new TokenDTO();
        mockToken.setToken("mockJwtToken");

        when(userService.login(userLogin)).thenReturn(mockToken);
        // Act
        ResponseEntity<TokenDTO> response = this.restTemplate.postForEntity(url, userLogin, TokenDTO.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mockJwtToken", response.getBody().getToken());
    }

    @Test
    void testCheckUsernameAvailability() {
        // Arrange
        String username = "availableUser";
        String url = this.getUrl() + "/users/checkUsernameAvailability/" + username;

        when(userService.existsByUsername(username)).thenReturn(false);

        // Act
        ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody());
    }

    @Test
    void testUpdateUser() throws IllegalAccessException {
        // Arrange
        Long userId = 1L;
        UserRegister userRegister = new UserRegister();
        userRegister.setUsername("updatedUser");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername("updatedUser");

        when(userService.update(anyLong(), any())).thenReturn(updatedUser);

        String url = this.getUrl() + "/users/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRegister> request = new HttpEntity<>(userRegister, headers);

        // Act
        ResponseEntity<UserDto> response = restTemplate.exchange(url, HttpMethod.PUT, request, UserDto.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updatedUser", response.getBody().getUsername());
    }
}

