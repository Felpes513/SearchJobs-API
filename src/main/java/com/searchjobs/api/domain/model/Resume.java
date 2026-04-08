package com.searchjobs.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume {
    private Long id;
    private Long userId;
    private String fileName;
    private String filePath;
    private String extractedText;
    private String parsedJson;
    private LocalDateTime createdAt;
}
