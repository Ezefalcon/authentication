package com.efalcon.authentication.model;


import jakarta.persistence.*;
import lombok.Data;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** Person name. */
    private String name;
    /** Person last name. */
    private String lastname;
    /** Username. */
    private String username;
    /** Encrypted password. */
    private String password;
    /** User email */
    private String email;
    /** Picture URL */
    private String picture;
    /** Is deleted */
    private boolean isDeleted;

    /** Roles */
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roles;

    /** Users can have multiple linked accounts, if no provider is set, the account was created in the app */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserProvider> providers;

    public boolean isLocalAccount() {
        return providers.isEmpty();
    }
}
