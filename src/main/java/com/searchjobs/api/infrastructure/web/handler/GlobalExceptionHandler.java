package com.searchjobs.api.infrastructure.web.handler;

import com.searchjobs.api.domain.exception.EmailAlreadyExistsException;
import com.searchjobs.api.domain.exception.MissingApiKeyException;
import com.searchjobs.api.domain.exception.ResumeNotFoundException;
import com.openai.errors.OpenAIException;
import com.openai.errors.PermissionDeniedException;
import com.openai.errors.RateLimitException;
import com.openai.errors.UnauthorizedException;
import com.openai.errors.UnprocessableEntityException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import com.searchjobs.api.infrastructure.web.handler.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ResumeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResumeNotFound(
            ResumeNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MissingApiKeyException.class)
    public ResponseEntity<ErrorResponse> handleMissingApiKey(
            MissingApiKeyException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos", request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> campos = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            campos.put(error.getField(), error.getDefaultMessage());
        }
        return buildValidation(request.getRequestURI(), campos);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSize(
            MaxUploadSizeExceededException ex, HttpServletRequest request) {
        return build(HttpStatus.PAYLOAD_TOO_LARGE, "Arquivo muito grande. Tamanho máximo permitido: 10MB", request.getRequestURI());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleOpenAiUnauthorized(
            UnauthorizedException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_GATEWAY, "Serviço de IA indisponível: credencial da API inválida ou ausente.", request.getRequestURI());
    }

    @ExceptionHandler(PermissionDeniedException.class)
    public ResponseEntity<ErrorResponse> handleOpenAiPermission(
            PermissionDeniedException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_GATEWAY, "Serviço de IA indisponível: sem permissão para acessar o recurso solicitado.", request.getRequestURI());
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(
            RateLimitException ex, HttpServletRequest request) {
        boolean isQuotaExceeded = ex.code()
                .map(code -> code.equals("insufficient_quota"))
                .orElse(false);
        if (isQuotaExceeded) {
            return build(HttpStatus.SERVICE_UNAVAILABLE, "Créditos da API de IA esgotados. Verifique o saldo da conta OpenAI.", request.getRequestURI());
        }
        return build(HttpStatus.SERVICE_UNAVAILABLE, "Serviço de IA temporariamente indisponível. Tente novamente em alguns instantes.", request.getRequestURI());
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ErrorResponse> handleOpenAiUnprocessable(
            UnprocessableEntityException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_GATEWAY, "O serviço de IA não conseguiu processar a requisição. Tente novamente.", request.getRequestURI());
    }

    @ExceptionHandler(OpenAIException.class)
    public ResponseEntity<ErrorResponse> handleOpenAi(
            OpenAIException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_GATEWAY, "Erro ao comunicar com o serviço de IA", request.getRequestURI());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(
            RuntimeException ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor", request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String mensagem, String path) {
        return ResponseEntity.status(status).body(
                ErrorResponse.builder()
                        .status(status.value())
                        .erro(status.getReasonPhrase())
                        .mensagem(mensagem)
                        .path(path)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    private ResponseEntity<ErrorResponse> buildValidation(String path, Map<String, String> campos) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(
                ErrorResponse.builder()
                        .status(422)
                        .erro("Validation Error")
                        .mensagem("Erro de validação nos campos")
                        .campos(campos)
                        .path(path)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}