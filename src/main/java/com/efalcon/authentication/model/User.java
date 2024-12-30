package com.efalcon.authentication.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by efalcon
 * User model
 */
@Entity(name = "users")
@Table(indexes={@Index(columnList="id,username",name="Index_user")})
@Data
public class User {
    @Id
    @GeneratedValue(generator="system-uuid")
    private String id;
    /** Person name. */
    private String name;
    /** Person last name. */
    private String lastname;
    /** Username. */
    private String username;
    /** Encrypted password. */
    private String password;
    /** Roles */
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;
    /** Users can have multiple linked accounts */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserProvider> providers;
    /** Is admin. */
    private Boolean isAdmin = false;
}
