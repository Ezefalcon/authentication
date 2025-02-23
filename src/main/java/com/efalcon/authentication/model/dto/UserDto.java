package com.efalcon.authentication.model.dto;

import com.efalcon.authentication.model.Role;
import com.efalcon.authentication.model.UserProvider;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by efalcon
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String lastname;
    private String username;
    private String email;
    private String picture;
    private List<Role> roles;
    private List<UserProvider> providers;
}
