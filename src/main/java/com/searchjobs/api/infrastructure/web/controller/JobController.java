package com.searchjobs.api.infrastructure.web.controller;

import com.searchjobs.api.application.dto.response.JobMatchResponse;
import com.searchjobs.api.application.dto.response.JobResponse;
import com.searchjobs.api.domain.port.in.JobMatchUseCase;
import com.searchjobs.api.domain.port.in.JobSearchUseCase;
import com.searchjobs.api.infrastructure.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobSearchUseCase jobSearchUseCase;
    private final JwtService jwtService;
    private final JobMatchUseCase jobMatchUseCase;

    @GetMapping("/search")
    public ResponseEntity<List<JobResponse>> search(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(jobSearchUseCase.searchJobsForUser(userId));
    }

    @PostMapping("/match-all")
    public ResponseEntity<List<JobMatchResponse>> matchAll(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(jobMatchUseCase.matchAll(userId));
    }

    @GetMapping("/matches")
    public ResponseEntity<List<JobMatchResponse>> getMatches(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(jobMatchUseCase.getMatches(userId));
    }
}