package com.searchjobs.api.application.dto.request;

import lombok.Getter;

@Getter
public class UpdateUserSettingsRequest {
    private String language;
    private String theme;
    private Boolean termsAccepted;
    private String openAiApiKey;
    private String jsearchApiKey;
}
