package com.searchjobs.api.domain.port.in;

import com.searchjobs.api.application.dto.request.UpdateUserSettingsRequest;
import com.searchjobs.api.application.dto.response.UserSettingsResponse;

public interface UserSettingsUseCase {
    UserSettingsResponse getSettings(Long userId);
    UserSettingsResponse updateSettings(Long userId, UpdateUserSettingsRequest request);
    UserSettingsResponse patchSettings(Long userId, UpdateUserSettingsRequest request);
}
