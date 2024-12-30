package com.efalcon.authentication.handler;

import com.efalcon.authentication.model.dto.ErrorDTO;
import com.efalcon.authentication.service.exceptions.AlreadyExistsException;
import com.efalcon.authentication.service.exceptions.UsernameOrPasswordInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Created by efalcon
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public void badIdExceptionHandler() {
        // Nothing to repos
    }

    @ExceptionHandler(UsernameOrPasswordInvalidException.class)
    public ResponseEntity<ErrorDTO> invalidLoginHandler(UsernameOrPasswordInvalidException exc) {
        return generateResponse(exc, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorDTO> invalidSingUpHandler(AlreadyExistsException exc) {
        return generateResponse(exc, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<ErrorDTO> accessForbiddenException(IllegalAccessException exc) {
        return generateResponse(exc, HttpStatus.FORBIDDEN);
    }

    private static ResponseEntity<ErrorDTO> generateResponse(Exception exc, HttpStatus httpStatus) {
        ErrorDTO errorDTO = new ErrorDTO(exc);
        return new ResponseEntity(errorDTO, httpStatus);
    }
}
