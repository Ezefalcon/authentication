package com.efalcon.authentication.service;


import com.efalcon.authentication.model.Provider;
import com.efalcon.authentication.model.User;
import com.efalcon.authentication.model.dto.UserTokenDto;

public interface TokenService {
    String generateToken(User user, Provider provider);

    UserTokenDto parseToken(String token);
}