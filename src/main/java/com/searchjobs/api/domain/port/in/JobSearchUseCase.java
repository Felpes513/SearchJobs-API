package com.searchjobs.api.domain.port.in;

import com.searchjobs.api.application.dto.response.JobResponse;

import java.util.List;

public interface JobSearchUseCase {
    List<JobResponse> searchJobsForUser(Long userId);
    void evictJobsCache(Long userId);

}