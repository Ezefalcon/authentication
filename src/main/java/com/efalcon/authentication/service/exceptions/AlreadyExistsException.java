package com.efalcon.authentication.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by efalcon
 */
@ResponseStatus(value = HttpStatus.IM_USED)
public class AlreadyExistsException extends IllegalArgumentException {
    public AlreadyExistsException(String entityName) {
        super(entityName + " with provided id already exists");
    }

    public AlreadyExistsException(String entityName, String customText) {
        super(entityName + customText);
    }
}
