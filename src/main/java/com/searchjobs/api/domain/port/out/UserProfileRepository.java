package com.searchjobs.api.domain.port.out;

import com.searchjobs.api.domain.model.UserProfile;

import java.util.Optional;

public interface UserProfileRepository {
    UserProfile save(UserProfile profile);
    Optional<UserProfile> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}