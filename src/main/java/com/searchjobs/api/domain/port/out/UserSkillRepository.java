package com.searchjobs.api.domain.port.out;

import com.searchjobs.api.domain.model.UserSkill;

import java.util.List;

public interface UserSkillRepository {
    void saveAll(Long userId, List<String> skills);
    void deleteByUserId(Long userId);
    List<UserSkill> findAllByUserId(Long userId);
}