package com.efalcon.authentication.service;

import com.efalcon.authentication.model.User;
import com.efalcon.authentication.model.dto.UserTokenDto;

public interface TokenService {
    String generateToken(User user);

    UserTokenDto parseToken(String token);
}