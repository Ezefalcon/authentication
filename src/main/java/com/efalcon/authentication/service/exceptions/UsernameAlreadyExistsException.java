package com.efalcon.authentication.service.exceptions;

/**
 * Created by efalcon
 */
public class UsernameAlreadyExistsException extends AlreadyExistsException {
    public UsernameAlreadyExistsException() {
        super("Username", " already exists");
    }
}
