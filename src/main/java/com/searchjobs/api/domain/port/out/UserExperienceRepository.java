package com.searchjobs.api.domain.port.out;

import com.searchjobs.api.domain.model.UserExperience;

import java.util.List;

public interface UserExperienceRepository {
    void saveAll(List<UserExperience> experiences);
    void deleteByUserId(Long userId);
    List<UserExperience> findAllByUserId(Long userId);
}