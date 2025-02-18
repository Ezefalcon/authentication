package com.efalcon.authentication.service;

import com.efalcon.authentication.model.Provider;
import com.efalcon.authentication.model.Role;
import com.efalcon.authentication.model.User;
import com.efalcon.authentication.model.dto.UserTokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.expiration}")
    private Integer expiration;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public String generateToken(User user) {
        Instant expirationTime = Instant.now().plus(expiration, ChronoUnit.HOURS);
        Date expirationDate = Date.from(expirationTime);

        Key key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .claim("id", user.getId())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles())
                .claim("provider", Provider.JWT)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public UserTokenDto parseToken(String token) {
        byte[] secretBytes = secret.getBytes();

        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                .setSigningKey(secretBytes)
                .build()
                .parseClaimsJws(token);

        Claims body = jwsClaims.getBody();

        String username = body.get("username", String.class);
        Long userId = body.get("id", Long.class);
        String email = body.get("email", String.class);
        List<Role> roles = body.get("roles", List.class);
        Provider provider = Provider.JWT;

        return new UserTokenDto(userId, username, email, roles, provider);
    }
}
