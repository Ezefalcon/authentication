package com.efalcon.authentication.service;

import com.efalcon.authentication.model.Provider;
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
    public String generateToken(User user, Provider provider) {
        Instant expirationTime = Instant.now().plus(expiration, ChronoUnit.HOURS);
        Date expirationDate = Date.from(expirationTime);

        Key key = Keys.hmacShaKeyFor(secret.getBytes());

        String compactTokenString = Jwts.builder()
                .claim("id", user.getId())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles())
                .claim("provider", provider)
                .setExpiration(expirationDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return "Bearer " + compactTokenString;
    }

    @Override
    public UserTokenDto parseToken(String token) {
        byte[] secretBytes = secret.getBytes();

        Jws<Claims> jwsClaims = Jwts.parserBuilder()
                .setSigningKey(secretBytes)
                .build()
                .parseClaimsJws(token);

        Claims body = jwsClaims.getBody();

        String username = body.getSubject();
        Long userId = body.get("id", Long.class);
        String email = body.get("email", String.class);
        List<Long> roles = body.get("roles", List.class);
        Provider provider = body.get("provider", Provider.class);

        return new UserTokenDto(userId, username, email, roles, provider);
    }
}