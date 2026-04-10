package com.searchjobs.api.application.dto.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubRepoDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("language")
    private String language;

    @JsonProperty("fork")
    private Boolean fork;

    @JsonProperty("private")
    private Boolean isPrivate;
}