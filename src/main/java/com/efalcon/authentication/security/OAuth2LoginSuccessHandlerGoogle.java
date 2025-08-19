package com.efalcon.authentication.security;

import com.efalcon.authentication.model.Provider;
import com.efalcon.authentication.model.Role;
import com.efalcon.authentication.model.User;
import com.efalcon.authentication.model.UserProvider;
import com.efalcon.authentication.service.TokenService;
import com.efalcon.authentication.service.UserService;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandlerGoogle extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${redirect.after.login}")
    private String redirectAfterLogin;

    private final UserService userService;
    private final TokenService tokenService;

    public OAuth2LoginSuccessHandlerGoogle(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();

        // Extract user details
        String email = oauthUser.getAttribute("email");

        Optional<User> byEmail = userService.findByEmail(email);
        User user;
        if (byEmail.isEmpty()) {
            user = new User();
            user.setEmail(email);
            user.setName(oauthUser.getAttribute("name"));
            user.setLastname(oauthUser.getAttribute("name"));
            user.setPicture(oauthUser.getAttribute("picture"));
            user.setPassword(RandomString.make(64));
            user.setRoles(List.of(Role.USER_GOOGLE));

            UserProvider userProvider = new UserProvider();
            userProvider.setProvider(Provider.GOOGLE);
            userProvider.setProviderUserId(oauthUser.getAttribute("id"));
            userProvider.setUser(user);
            user.setProviders(List.of(userProvider));

            this.userService.save(user);
        } else {
            user = byEmail.get();
        }

        // Generate JWT
        String jwt = tokenService.generateToken(user);

        redirectToAndSetToken(response, jwt, redirectAfterLogin);
    }

    private void sendJWTInResponse(HttpServletResponse response, String jwt) throws IOException {
        response.getWriter().write(jwt);
        response.getWriter().flush();
    }

    private void redirectToAndSetToken(HttpServletResponse response, String jwt, String redirectTo) throws IOException {
        String frontendUrl = redirectTo + "?token=" + jwt;
        response.sendRedirect(frontendUrl);
    }
}