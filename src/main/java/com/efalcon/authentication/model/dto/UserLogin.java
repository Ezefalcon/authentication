package com.efalcon.authentication.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by efalcon
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLogin {
    @NotNull private String username;
    @NotNull private String password;
}
