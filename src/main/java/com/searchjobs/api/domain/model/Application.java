package com.searchjobs.api.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {
    private Long id;
    private Long userId;
    private Long jobId;
    private String status;
    private String observacao;
    private LocalDateTime dataCandidatura;
    private LocalDateTime createdAt;
}