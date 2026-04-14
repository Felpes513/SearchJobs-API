package com.searchjobs.api.infrastructure.web.controller;

import com.searchjobs.api.application.dto.request.UpdateUserSettingsRequest;
import com.searchjobs.api.application.dto.response.UserSettingsResponse;
import com.searchjobs.api.domain.port.in.UserSettingsUseCase;
import com.searchjobs.api.infrastructure.security.JwtService;
import com.searchjobs.api.infrastructure.web.handler.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class UserSettingsController {

    private final UserSettingsUseCase userSettingsUseCase;
    private final JwtService jwtService;

    private Long extractUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtService.extractUserId(token);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserSettingsResponse>> getSettings(HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Configurações obtidas com sucesso",
                        userSettingsUseCase.getSettings(extractUserId(request)))
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserSettingsResponse>> updateSettings(
            @RequestBody UpdateUserSettingsRequest body,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Configurações atualizadas com sucesso",
                        userSettingsUseCase.updateSettings(extractUserId(request), body))
        );
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserSettingsResponse>> patchSettings(
            @RequestBody UpdateUserSettingsRequest body,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Configurações atualizadas com sucesso",
                        userSettingsUseCase.patchSettings(extractUserId(request), body))
        );
    }
}
