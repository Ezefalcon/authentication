package com.efalcon.authentication.service.exceptions;

/**
 * Created by efalcon
 */
public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super("User");
    }
}
