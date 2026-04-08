package com.searchjobs.api.domain.port.out;

public interface AiExtractionPort {
    String extractResumeData(String resumeText);
}