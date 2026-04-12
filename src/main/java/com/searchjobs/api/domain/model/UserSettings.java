package com.searchjobs.api.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserSettings {
    private Long id;
    private Long userId;
    private String language;
    private String theme;
    private boolean termsAccepted;
    private String openAiApiKey;    // plain text in domain, encrypted at rest
    private String jsearchApiKey;   // plain text in domain, encrypted at rest
    private LocalDateTime updatedAt;
}
