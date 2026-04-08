package com.searchjobs.api.domain.port.out;

public interface UserSkillRepository {
    void saveAll(Long userId, java.util.List<String> skills);
    void deleteByUserId(Long userId);
}