package com.searchjobs.api.domain.port.out;

import com.searchjobs.api.domain.model.Job;

import java.util.List;
import java.util.Optional;

public interface JobRepository {
    Job save(Job job);
    Optional<Job> findByExternalId(String externalId);
    List<Job> findAll();
    Optional<Job> findById(Long id);
}