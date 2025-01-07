package com.efalcon.authentication.model.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonPropertyOrder({"message", "cause", "dateTime"})
public class ErrorDTO {
    private String message;
    private String cause;
    private LocalDateTime dateTime;

    // For debugging purposes
    public ErrorDTO(Exception exc) {
        if(exc.getStackTrace().length > 0) {
            this.cause = exc.getStackTrace()[0].toString();
        }
        this.message = exc.getMessage();
        this.dateTime = LocalDateTime.now();
    }
}
