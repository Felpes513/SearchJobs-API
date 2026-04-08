package com.searchjobs.api.domain.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email já cadastrado: " + email);
    }
}