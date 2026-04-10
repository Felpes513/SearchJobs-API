package com.searchjobs.api.infrastructure.external;

import com.searchjobs.api.application.dto.internal.GitHubRepoDto;
import com.searchjobs.api.domain.model.UserProject;
import com.searchjobs.api.domain.port.out.GitHubPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class GitHubAdapter implements GitHubPort {

    private final RestClient restClient;

    public GitHubAdapter(@Value("${github.token:}") String token) {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28");

        if (token != null && !token.isBlank()) {
            builder.defaultHeader("Authorization", "Bearer " + token);
        }

        this.restClient = builder.build();
    }

    @Override
    public List<UserProject> fetchProjects(String githubUsername) {
        List<GitHubRepoDto> repos = restClient.get()
                .uri("/users/{username}/repos?per_page=30&sort=updated", githubUsername)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (repos == null) return Collections.emptyList();

        return repos.stream()
                .filter(repo -> !Boolean.TRUE.equals(repo.getFork()))
                .filter(repo -> !Boolean.TRUE.equals(repo.getIsPrivate()))
                .map(repo -> {
                    String stack = fetchLanguages(githubUsername, repo.getName());
                    return UserProject.builder()
                            .nome(repo.getName())
                            .descricao(repo.getDescription())
                            .stack(stack)
                            .link(repo.getHtmlUrl())
                            .build();
                })
                .toList();
    }

    private String fetchLanguages(String username, String repoName) {
        try {
            Map<String, Long> languages = restClient.get()
                    .uri("/repos/{username}/{repo}/languages", username, repoName)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (languages == null || languages.isEmpty()) return null;

            return String.join(", ", languages.keySet());
        } catch (Exception e) {
            return null;
        }
    }
}