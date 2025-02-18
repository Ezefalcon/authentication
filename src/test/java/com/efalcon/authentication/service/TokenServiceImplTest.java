package com.efalcon.authentication.service;

import com.efalcon.authentication.model.Provider;
import com.efalcon.authentication.model.Role;
import com.efalcon.authentication.model.User;
import com.efalcon.authentication.model.dto.UserTokenDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {

    @InjectMocks
    private TokenServiceImpl tokenService;

    private final String secret = "mySuperSecretKeyForJwtSigningMySuperSecretKeyForJwtSigning";
    private final int expiration = 1;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "secret", secret);
        ReflectionTestUtils.setField(tokenService, "expiration", expiration);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoles(Collections.singletonList(Role.USER));

        String token = tokenService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void parseToken_ShouldReturnValidUserTokenDto() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoles(Collections.singletonList(Role.USER));

        String token = tokenService.generateToken(user);
        assertNotNull(token);

        UserTokenDto userTokenDto = tokenService.parseToken(token);
        assertNotNull(userTokenDto);
        assertEquals(user.getId(), userTokenDto.getId());
        assertEquals(user.getUsername(), userTokenDto.getUsername());
        assertEquals(Provider.JWT, userTokenDto.getProvider());
    }
}
