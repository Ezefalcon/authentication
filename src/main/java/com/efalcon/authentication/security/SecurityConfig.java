package com.efalcon.authentication.security;


import com.efalcon.authentication.service.TokenService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by efalcon
 */
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true) // jsr250 is for roles
@Configuration
public class SecurityConfig {

    private static final String[] PERMIT_ENDPOINTS = {
        "/users/**",
        "/users",
        "/v3/api-docs/**",
        "/api-docs/**",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/login/success",
        "/",
    };

    /**
     * Google OAuth2 flow only on dedicated endpoints.
     */
    @Bean
    @Order(1)
    @ConditionalOnProperty(name = "spring.security.oauth2.enabled")
    public SecurityFilterChain googleFilterChain(HttpSecurity http,
                                                 GoogleOAuthAuthenticationSuccessHandler oAuth2LoginSuccessHandlerGoogle) throws Exception {
        http
                .securityMatcher("/auth/google/**", "/oauth2/**", "/login/oauth2/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/google/**", "/oauth2/**", "/login/oauth2/**").permitAll()
                        .anyRequest().denyAll() // don’t let this chain cover other API calls
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth -> oauth.successHandler(oAuth2LoginSuccessHandlerGoogle));

        return http.build();
    }

    /**
     * Default JWT-based API security.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http, TokenService tokenService) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(matcher -> matcher
                        .requestMatchers(PERMIT_ENDPOINTS).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(AbstractHttpConfigurer::disable) // fully disable google here
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtAuthenticationFilter(tokenService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

