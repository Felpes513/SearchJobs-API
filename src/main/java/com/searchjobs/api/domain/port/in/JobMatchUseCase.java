package com.searchjobs.api.domain.port.in;

import com.searchjobs.api.application.dto.response.JobMatchResponse;
import java.util.List;

public interface JobMatchUseCase {
    List<JobMatchResponse> matchAll(Long userId);
    List<JobMatchResponse> getMatches(Long userId);
}