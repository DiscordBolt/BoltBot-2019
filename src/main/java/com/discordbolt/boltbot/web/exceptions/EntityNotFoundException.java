package com.discordbolt.boltbot.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Entity not found")
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String exception) {
        super(exception);
    }
}
