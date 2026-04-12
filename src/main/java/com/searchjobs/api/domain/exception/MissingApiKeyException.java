package com.searchjobs.api.domain.exception;

public class MissingApiKeyException extends RuntimeException {
    public MissingApiKeyException(String message) {
        super(message);
    }
}
