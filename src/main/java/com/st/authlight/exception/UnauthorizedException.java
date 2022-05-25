package com.st.authlight.exception;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class UnauthorizedException extends ResponseStatusException {

    public UnauthorizedException(String message) {
        super(UNAUTHORIZED, message);
    }
}