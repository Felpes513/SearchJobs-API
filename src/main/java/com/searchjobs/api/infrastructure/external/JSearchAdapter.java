package com.searchjobs.api.infrastructure.external;

import com.searchjobs.api.application.dto.internal.JSearchResponseDto;
import com.searchjobs.api.domain.model.Job;
import com.searchjobs.api.domain.port.out.JobSearchPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class JSearchAdapter implements JobSearchPort {

    private final RestClient restClient;
    private final String apiKey;
    private final String apiHost;

    public JSearchAdapter(
            @Value("${jsearch.api-key}") String apiKey,
            @Value("${jsearch.api-host}") String apiHost,
            @Value("${jsearch.base-url}") String baseUrl
    ) {
        this.apiKey = apiKey;
        this.apiHost = apiHost;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public List<Job> search(String query) {
        JSearchResponseDto response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("query", query)
                        .queryParam("page", "1")
                        .queryParam("num_pages", "1")
                        .queryParam("date_posted", "all")
                        .build())
                .header("x-rapidapi-key", apiKey)
                .header("x-rapidapi-host", apiHost)
                .header("Content-Type", "application/json")
                .retrieve()
                .body(JSearchResponseDto.class);

        if (response == null || response.getData() == null) {
            return Collections.emptyList();
        }

        return response.getData().stream()
                .map(this::toDomain)
                .toList();
    }

    private Job toDomain(JSearchResponseDto.JobDataDto dto) {
        return Job.builder()
                .externalId(dto.getJobId())
                .titulo(dto.getJobTitle())
                .empresa(dto.getEmployerName())
                .localizacao(dto.getLocalizacao())
                .modeloTrabalho(dto.getJobIsRemote() != null && dto.getJobIsRemote() ? "REMOTO" : "PRESENCIAL")
                .descricao(dto.getJobDescription())
                .salario(dto.getSalario())
                .jobUrl(dto.getJobApplyLink())
                .dataPublicacao(parseDate(dto.getJobPostedAtDatetimeUtc()))
                .build();
    }

    private LocalDateTime parseDate(String date) {
        if (date == null) return null;
        try {
            return LocalDateTime.parse(date.replace("Z", ""));
        } catch (Exception e) {
            return null;
        }
    }
}