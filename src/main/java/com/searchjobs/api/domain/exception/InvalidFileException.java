package com.searchjobs.api.domain.exception;

public class InvalidFileException extends RuntimeException {
    public InvalidFileException(String mensagem) {
        super(mensagem);
    }
}