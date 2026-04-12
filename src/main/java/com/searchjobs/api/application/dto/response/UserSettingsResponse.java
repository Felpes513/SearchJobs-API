package com.searchjobs.api.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSettingsResponse {
    private String language;
    private String theme;
    private boolean termsAccepted;
    private String openAiApiKeyMasked;
    private String jsearchApiKeyMasked;
    private boolean hasOpenAiApiKey;
    private boolean hasJsearchApiKey;
    private String updatedAt;
}
