package com.efalcon.authentication.model.dto;


import com.efalcon.authentication.model.Provider;
import com.efalcon.authentication.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * User information for Token.
 */
@Data
@AllArgsConstructor
public class UserTokenDto {
    private Long id;
    private String username;
    private String email;
    private List<Role> roles;
    private Provider provider;
}