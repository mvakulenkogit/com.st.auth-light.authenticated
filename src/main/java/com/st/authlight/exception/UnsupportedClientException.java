package com.st.authlight.exception;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UnsupportedClientException extends ResponseStatusException {

    public UnsupportedClientException(String message) {
        super(BAD_REQUEST, message);
    }
}