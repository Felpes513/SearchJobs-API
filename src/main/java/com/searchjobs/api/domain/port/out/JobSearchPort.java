package com.searchjobs.api.domain.port.out;

import com.searchjobs.api.domain.model.Job;

import java.util.List;

public interface JobSearchPort {
    List<Job> search(String query);
}