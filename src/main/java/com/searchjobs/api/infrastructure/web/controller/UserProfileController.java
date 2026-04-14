package com.searchjobs.api.infrastructure.web.controller;

import com.searchjobs.api.application.dto.request.*;
import com.searchjobs.api.application.dto.response.*;
import com.searchjobs.api.domain.port.in.GitHubSyncUseCase;
import com.searchjobs.api.domain.port.in.UserProfileUseCase;
import com.searchjobs.api.infrastructure.security.JwtService;
import com.searchjobs.api.infrastructure.web.handler.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileUseCase userProfileUseCase;
    private final JwtService jwtService;
    private final GitHubSyncUseCase gitHubSyncUseCase;

    private Long extractUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtService.extractUserId(token);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Perfil obtido com sucesso",
                        userProfileUseCase.getProfile(extractUserId(request)))
        );
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @RequestBody UpdateProfileRequest body,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Perfil atualizado com sucesso",
                        userProfileUseCase.updateProfile(extractUserId(request), body))
        );
    }

    @GetMapping("/skills")
    public ResponseEntity<ApiResponse<List<UserSkillResponse>>> getSkills(HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Skills obtidas com sucesso",
                        userProfileUseCase.getSkills(extractUserId(request)))
        );
    }

    @PutMapping("/skills")
    public ResponseEntity<ApiResponse<List<UserSkillResponse>>> updateSkills(
            @Valid @RequestBody UpdateSkillsRequest body,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Skills atualizadas com sucesso",
                        userProfileUseCase.updateSkills(extractUserId(request), body))
        );
    }

    @GetMapping("/experiences")
    public ResponseEntity<ApiResponse<List<UserExperienceResponse>>> getExperiences(HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Experiências obtidas com sucesso",
                        userProfileUseCase.getExperiences(extractUserId(request)))
        );
    }

    @PutMapping("/experiences")
    public ResponseEntity<ApiResponse<List<UserExperienceResponse>>> updateExperiences(
            @RequestBody UpdateExperiencesRequest body,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Experiências atualizadas com sucesso",
                        userProfileUseCase.updateExperiences(extractUserId(request), body))
        );
    }

    @GetMapping("/certifications")
    public ResponseEntity<ApiResponse<List<UserCertificationResponse>>> getCertifications(HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Certificações obtidas com sucesso",
                        userProfileUseCase.getCertifications(extractUserId(request)))
        );
    }

    @PutMapping("/certifications")
    public ResponseEntity<ApiResponse<List<UserCertificationResponse>>> updateCertifications(
            @RequestBody UpdateCertificationsRequest body,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Certificações atualizadas com sucesso",
                        userProfileUseCase.updateCertifications(extractUserId(request), body))
        );
    }

    @GetMapping("/projects")
    public ResponseEntity<ApiResponse<List<UserProjectResponse>>> getProjects(HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Projetos obtidos com sucesso",
                        userProfileUseCase.getProjects(extractUserId(request)))
        );
    }

    @PutMapping("/projects")
    public ResponseEntity<ApiResponse<List<UserProjectResponse>>> updateProjects(
            @RequestBody UpdateProjectsRequest body,
            HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Projetos atualizados com sucesso",
                        userProfileUseCase.updateProjects(extractUserId(request), body))
        );
    }

    @PostMapping("/github/sync")
    public ResponseEntity<ApiResponse<List<UserProjectResponse>>> syncGitHub(HttpServletRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Projetos sincronizados com o GitHub com sucesso",
                        gitHubSyncUseCase.sync(extractUserId(request)))
        );
    }
}
