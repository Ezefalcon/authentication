package com.efalcon.authentication.model.dto;


import com.efalcon.authentication.model.Provider;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * User information for Token.
 */
@Data
@AllArgsConstructor
public class UserTokenDto {
    private long id;
    private String username;
    private String email;
    private List<Long> roles;
    private Provider provider;
}