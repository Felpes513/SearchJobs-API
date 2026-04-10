package com.searchjobs.api.application.service;

import com.searchjobs.api.application.dto.response.UserProjectResponse;
import com.searchjobs.api.domain.exception.ResumeNotFoundException;
import com.searchjobs.api.domain.model.UserProject;
import com.searchjobs.api.domain.port.in.GitHubSyncUseCase;
import com.searchjobs.api.domain.port.out.GitHubPort;
import com.searchjobs.api.domain.port.out.UserProfileRepository;
import com.searchjobs.api.domain.port.out.UserProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubSyncService implements GitHubSyncUseCase {

    private final GitHubPort gitHubPort;
    private final UserProfileRepository userProfileRepository;
    private final UserProjectRepository userProjectRepository;

    @Override
    @Transactional
    public List<UserProjectResponse> sync(Long userId) {
        String githubUrl = userProfileRepository.findByUserId(userId)
                .map(p -> p.getGithubUrl())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Configure seu GitHub no perfil antes de sincronizar"));

        if (githubUrl == null || githubUrl.isBlank()) {
            throw new IllegalArgumentException(
                    "Configure seu GitHub no perfil antes de sincronizar");
        }

        String username = extractUsername(githubUrl);
        List<UserProject> projects = gitHubPort.fetchProjects(username);

        if (projects.isEmpty()) {
            throw new IllegalArgumentException(
                    "Nenhum repositório público encontrado para o usuário: " + username);
        }

        userProjectRepository.deleteByUserId(userId);

        List<UserProject> withUserId = projects.stream()
                .map(p -> UserProject.builder()
                        .userId(userId)
                        .nome(p.getNome())
                        .descricao(p.getDescricao())
                        .stack(p.getStack())
                        .link(p.getLink())
                        .build())
                .toList();

        userProjectRepository.saveAll(withUserId);

        return userProjectRepository.findAllByUserId(userId)
                .stream()
                .map(p -> UserProjectResponse.builder()
                        .id(p.getId())
                        .nome(p.getNome())
                        .descricao(p.getDescricao())
                        .stack(p.getStack())
                        .link(p.getLink())
                        .build())
                .toList();
    }

    private String extractUsername(String githubUrl) {
        String cleaned = githubUrl
                .trim()
                .replaceAll("/$", "");

        if (cleaned.contains("github.com/")) {
            return cleaned.substring(cleaned.lastIndexOf("github.com/") + 11);
        }

        return cleaned;
    }
}