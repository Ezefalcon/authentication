package com.efalcon.authentication.controller;

import com.efalcon.authentication.annotation.SameUserOrAdminAccessOnly;
import com.efalcon.authentication.model.User;
import com.efalcon.authentication.model.dto.*;
import com.efalcon.authentication.service.UserService;
import com.efalcon.authentication.service.exceptions.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import com.efalcon.authentication.util.GenericMapper;

import java.util.Optional;

/**
 * Created by efalcon
 */
@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    private final GenericMapper<User, UserDto> userMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.userMapper = new GenericMapper<>(User.class, UserDto.class);
    }

    @GetMapping("/{id}")
    @SameUserOrAdminAccessOnly
    public UserDto findById(@PathVariable("id") Long id) {
        Optional<User> byId = this.userService.findById(id);
        if (byId.isPresent()) {
            return userMapper.convertToDTO(byId.get());
        }
        throw new UserNotFoundException();
    }

    /**
     * Following the conventions of REST API, but it can be changed
     * to /register
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@RequestBody UserRegister user) {
        User savedUser = this.userService.save(convertToEntity(user));
        return userMapper.convertToDTO(savedUser);
    }

    @PostMapping("/login")
    public TokenDTO login(@RequestBody UserLogin userLogin) {
        return this.userService.login(userLogin);
    }

    @GetMapping("/login/success")
    public String loginSuccess() {
        return "This is just shows that you have successfully logged in. You can see the token in the URL, you can change the" +
                "redirect URL in application.yml";
    }

    @GetMapping("/checkUsernameAvailability/{username}")
    public boolean checkUserAvailability(@PathVariable String username) {
        return !this.userService.existsByUsername(username);
    }

    @SameUserOrAdminAccessOnly
    @PutMapping(value = "/{id}", name = "id")
    public UserDto update(@RequestBody UserRegister user, @PathVariable Long id) throws IllegalAccessException {
        User updatedUser = this.userService.update(id, convertToEntity(user));
        return userMapper.convertToDTO(updatedUser);
    }

    @SameUserOrAdminAccessOnly
    @DeleteMapping(value = "/{id}", name = "id")
    public void delete(@PathVariable Long id) {
        this.userService.removeById(id);
    }

    @GetMapping("/info")
    public UserTokenDto userInfo(AbstractAuthenticationToken authentication) {
        return (UserTokenDto) authentication.getPrincipal();
    }

    private User convertToEntity(UserRegister userRegister) {
        return modelMapper.map(userRegister, User.class);
    }
}
