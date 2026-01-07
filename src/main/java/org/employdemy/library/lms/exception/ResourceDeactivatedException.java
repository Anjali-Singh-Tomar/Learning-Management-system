package org.employdemy.library.lms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceDeactivatedException extends RuntimeException{

    public ResourceDeactivatedException (String message) {
        super(message);
    }
}
