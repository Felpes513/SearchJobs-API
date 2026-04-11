package com.searchjobs.api.domain.port.out;

import com.searchjobs.api.domain.model.UserSettings;

import java.util.Optional;

public interface UserSettingsRepository {
    Optional<UserSettings> findByUserId(Long userId);
    UserSettings save(UserSettings settings);
}
