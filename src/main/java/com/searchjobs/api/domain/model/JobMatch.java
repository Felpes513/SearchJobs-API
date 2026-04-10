package com.searchjobs.api.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobMatch {
    private Long id;
    private Long userId;
    private Long jobId;
    private Double score;
    private String justificativa;
    private String pontosFortesJson;
    private String gapsJson;
    private LocalDateTime createdAt;
}