package com.searchjobs.api.domain.port.out;

import com.searchjobs.api.domain.model.JobMatch;
import java.util.List;

public interface JobMatchRepository {
    JobMatch save(JobMatch match);
    List<JobMatch> findAllByUserId(Long userId);
    void deleteByUserIdAndJobId(Long userId, Long jobId);
}