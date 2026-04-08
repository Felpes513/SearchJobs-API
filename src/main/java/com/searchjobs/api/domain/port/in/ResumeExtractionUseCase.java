package com.searchjobs.api.domain.port.in;

import com.searchjobs.api.application.dto.response.ResumeExtractionResponse;

public interface ResumeExtractionUseCase {
    ResumeExtractionResponse extract(Long resumeId);
}