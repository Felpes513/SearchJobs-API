package com.searchjobs.api.domain.port.out;

import com.searchjobs.api.domain.model.Application;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {
    Application save(Application application);
    List<Application> findAllByUserId(Long userId);
    Optional<Application> findByIdAndUserId(Long id, Long userId);
    void delete(Long id);
    boolean existsByUserIdAndJobId(Long userId, Long jobId);
}