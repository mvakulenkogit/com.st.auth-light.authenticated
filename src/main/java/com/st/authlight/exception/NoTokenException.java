package com.st.authlight.exception;

public class NoTokenException extends RuntimeException {
    public NoTokenException() {
        super("NO_AUTH");
    }
}
