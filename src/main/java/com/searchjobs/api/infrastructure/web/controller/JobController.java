package com.searchjobs.api.infrastructure.web.controller;

import com.searchjobs.api.application.dto.response.JobResponse;
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

    @GetMapping("/search")
    public ResponseEntity<List<JobResponse>> search(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(jobSearchUseCase.searchJobsForUser(userId));
    }
}