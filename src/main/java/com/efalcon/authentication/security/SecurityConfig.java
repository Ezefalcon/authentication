package com.efalcon.authentication.security;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Created by efalcon
 */
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true) // jsr250 is for roles
@Configuration
public class SecurityConfig {

    @Value("${spring.security.oauth2.enabled}")
    private Boolean oauth2Enabled;

    private static final String[] PERMIT_ENDPOINTS = {
        "/users/**",
        "/users",
        "/"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, OAuth2LoginSuccessHandlerGoogle oAuth2LoginSuccessHandlerGoogle) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(matcher ->
                        matcher.requestMatchers(PERMIT_ENDPOINTS).permitAll()
                                .anyRequest().authenticated()
                )
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth -> {
                    if (oauth2Enabled) {
                        oauth.successHandler(oAuth2LoginSuccessHandlerGoogle);
                    } else oauth.disable();
                })
                .build();
    }
}

