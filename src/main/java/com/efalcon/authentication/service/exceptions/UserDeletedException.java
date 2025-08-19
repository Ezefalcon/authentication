package com.efalcon.authentication.service.exceptions;

public class UserDeletedException extends IllegalArgumentException {
    public UserDeletedException() {
        super("User has been deleted");
    }
}
