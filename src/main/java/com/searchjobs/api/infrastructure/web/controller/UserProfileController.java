package com.searchjobs.api.infrastructure.web.controller;

import com.searchjobs.api.application.dto.request.*;
import com.searchjobs.api.application.dto.response.*;
import com.searchjobs.api.domain.port.in.GitHubSyncUseCase;
import com.searchjobs.api.domain.port.in.UserProfileUseCase;
import com.searchjobs.api.infrastructure.security.JwtService;
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
    public ResponseEntity<UserProfileResponse> getProfile(HttpServletRequest request) {
        return ResponseEntity.ok(userProfileUseCase.getProfile(extractUserId(request)));
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestBody UpdateProfileRequest body,
            HttpServletRequest request) {
        return ResponseEntity.ok(userProfileUseCase.updateProfile(extractUserId(request), body));
    }

    @GetMapping("/skills")
    public ResponseEntity<List<UserSkillResponse>> getSkills(HttpServletRequest request) {
        return ResponseEntity.ok(userProfileUseCase.getSkills(extractUserId(request)));
    }

    @PutMapping("/skills")
    public ResponseEntity<List<UserSkillResponse>> updateSkills(
            @Valid @RequestBody UpdateSkillsRequest body,
            HttpServletRequest request) {
        return ResponseEntity.ok(userProfileUseCase.updateSkills(extractUserId(request), body));
    }

    @GetMapping("/experiences")
    public ResponseEntity<List<UserExperienceResponse>> getExperiences(HttpServletRequest request) {
        return ResponseEntity.ok(userProfileUseCase.getExperiences(extractUserId(request)));
    }

    @PutMapping("/experiences")
    public ResponseEntity<List<UserExperienceResponse>> updateExperiences(
            @RequestBody UpdateExperiencesRequest body,
            HttpServletRequest request) {
        return ResponseEntity.ok(userProfileUseCase.updateExperiences(extractUserId(request), body));
    }

    @GetMapping("/certifications")
    public ResponseEntity<List<UserCertificationResponse>> getCertifications(HttpServletRequest request) {
        return ResponseEntity.ok(userProfileUseCase.getCertifications(extractUserId(request)));
    }

    @PutMapping("/certifications")
    public ResponseEntity<List<UserCertificationResponse>> updateCertifications(
            @RequestBody UpdateCertificationsRequest body,
            HttpServletRequest request) {
        return ResponseEntity.ok(userProfileUseCase.updateCertifications(extractUserId(request), body));
    }

    @GetMapping("/projects")
    public ResponseEntity<List<UserProjectResponse>> getProjects(HttpServletRequest request) {
        return ResponseEntity.ok(userProfileUseCase.getProjects(extractUserId(request)));
    }

    @PutMapping("/projects")
    public ResponseEntity<List<UserProjectResponse>> updateProjects(
            @RequestBody UpdateProjectsRequest body,
            HttpServletRequest request) {
        return ResponseEntity.ok(userProfileUseCase.updateProjects(extractUserId(request), body));
    }

    @PostMapping("/github/sync")
    public ResponseEntity<List<UserProjectResponse>> syncGitHub(HttpServletRequest request) {
        return ResponseEntity.ok(gitHubSyncUseCase.sync(extractUserId(request)));
    }
}