package com.searchjobs.api.domain.exception;

public class ResumeNotFoundException extends RuntimeException {
    public ResumeNotFoundException(Long id) {
        super("Currículo não encontrado com id: " + id);
    }
}