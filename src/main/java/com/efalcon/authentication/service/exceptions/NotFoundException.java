package com.efalcon.authentication.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by efalcon
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends IllegalArgumentException {
    public NotFoundException(String entityName) {
        super(entityName + " with the specified id does not exists");
    }
}
