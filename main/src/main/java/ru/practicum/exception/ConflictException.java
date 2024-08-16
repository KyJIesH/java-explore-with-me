package ru.practicum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

    private String message = "";

    public ConflictException(String message) {
        this.message = message;
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
