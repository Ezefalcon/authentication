package com.efalcon.authentication.controller;

import com.efalcon.authentication.model.User;
import com.efalcon.authentication.model.dto.TokenDTO;
import com.efalcon.authentication.model.dto.UserDto;
import com.efalcon.authentication.model.dto.UserLogin;
import com.efalcon.authentication.model.dto.UserRegister;
import com.efalcon.authentication.service.UserService;
import com.efalcon.authentication.service.exceptions.UserNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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
    public UserDto findById(@PathVariable("id") Long id) {
        Optional<User> byId = this.userService.findById(id);
        if (byId.isPresent()) {
            return userMapper.convertToDTO(byId.get());
        }
        throw new UserNotFoundException();
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@RequestBody UserRegister user) {
        User savedUser = this.userService.save(convertToEntity(user));
        return userMapper.convertToDTO(savedUser);
    }

    @PostMapping("/login")
    public TokenDTO login(@RequestBody UserLogin userLogin) {
        return this.userService.login(userLogin);
    }

    @GetMapping("/checkUsernameAvailability/{username}")
    public boolean checkUserAvailability(@PathVariable String username) {
        return !this.userService.existsByUsername(username);
    }

    private User convertToEntity(UserRegister userRegister) {
        return modelMapper.map(userRegister, User.class);
    }
}
