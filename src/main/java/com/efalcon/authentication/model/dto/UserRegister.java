package com.efalcon.authentication.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Created by efalcon
 */
@Data
public class UserRegister {
    private String name;
    private String lastname;
    @NotNull
    @Min(value = 4)
    @Max(value = 30)
    private String username;
    @NotNull
    @Min(value = 8)
    @Max(value = 120)
    private String password;
}
