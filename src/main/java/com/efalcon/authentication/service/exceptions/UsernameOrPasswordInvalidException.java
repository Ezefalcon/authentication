package com.efalcon.authentication.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by efalcon
 */
@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UsernameOrPasswordInvalidException extends IllegalArgumentException {
    public UsernameOrPasswordInvalidException() {
        super("Username or password invalid");
    }
}
