package com.efalcon.authentication.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by efalcon
 */
@Data
@AllArgsConstructor
public class UserLogin {
    @NotNull private String username;
    @NotNull private String password;
}
