package com.searchjobs.api.infrastructure.web.controller;

import com.searchjobs.api.application.dto.request.CreateApplicationRequest;
import com.searchjobs.api.application.dto.request.UpdateApplicationStatusRequest;
import com.searchjobs.api.application.dto.response.ApplicationKanbanResponse;
import com.searchjobs.api.domain.port.in.ApplicationUseCase;
import com.searchjobs.api.infrastructure.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationUseCase applicationUseCase;
    private final JwtService jwtService;

    private Long extractUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtService.extractUserId(token);
    }

    @GetMapping
    public ResponseEntity<ApplicationKanbanResponse> getKanban(HttpServletRequest request) {
        return ResponseEntity.ok(applicationUseCase.getKanban(extractUserId(request)));
    }

    @PostMapping
    public ResponseEntity<Void> create(
            @Valid @RequestBody CreateApplicationRequest body,
            HttpServletRequest request) {
        applicationUseCase.create(extractUserId(request), body);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateApplicationStatusRequest body,
            HttpServletRequest request) {
        applicationUseCase.updateStatus(extractUserId(request), id, body);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        applicationUseCase.delete(extractUserId(request), id);
        return ResponseEntity.noContent().build();
    }
}