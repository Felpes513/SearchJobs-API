package com.searchjobs.api.application.service;

import com.searchjobs.api.application.dto.request.UpdateUserSettingsRequest;
import com.searchjobs.api.application.dto.response.UserSettingsResponse;
import com.searchjobs.api.domain.model.UserSettings;
import com.searchjobs.api.domain.port.in.UserSettingsUseCase;
import com.searchjobs.api.domain.port.out.UserSettingsRepository;
import com.searchjobs.api.infrastructure.encryption.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSettingsService implements UserSettingsUseCase {

    private final UserSettingsRepository userSettingsRepository;
    private final EncryptionService encryptionService;

    @Override
    public UserSettingsResponse getSettings(Long userId) {
        UserSettings settings = userSettingsRepository.findByUserId(userId)
                .orElse(emptySettings(userId));
        return toResponse(settings);
    }

    @Override
    public UserSettingsResponse updateSettings(Long userId, UpdateUserSettingsRequest request) {
        UserSettings existing = userSettingsRepository.findByUserId(userId)
                .orElse(emptySettings(userId));

        // PUT: substitui language/theme/termsAccepted; chaves só são alteradas se enviadas
        UserSettings updated = UserSettings.builder()
                .id(existing.getId())
                .userId(userId)
                .language(request.getLanguage())
                .theme(request.getTheme())
                .termsAccepted(request.getTermsAccepted() != null && request.getTermsAccepted())
                .openAiApiKey(request.getOpenAiApiKey() != null
                        ? request.getOpenAiApiKey() : existing.getOpenAiApiKey())
                .jsearchApiKey(request.getJsearchApiKey() != null
                        ? request.getJsearchApiKey() : existing.getJsearchApiKey())
                .build();

        return toResponse(userSettingsRepository.save(updated));
    }

    @Override
    public UserSettingsResponse patchSettings(Long userId, UpdateUserSettingsRequest request) {
        UserSettings existing = userSettingsRepository.findByUserId(userId)
                .orElse(emptySettings(userId));

        // PATCH: mantém valores existentes para campos não enviados
        UserSettings patched = UserSettings.builder()
                .id(existing.getId())
                .userId(userId)
                .language(request.getLanguage() != null ? request.getLanguage() : existing.getLanguage())
                .theme(request.getTheme() != null ? request.getTheme() : existing.getTheme())
                .termsAccepted(request.getTermsAccepted() != null
                        ? request.getTermsAccepted() : existing.isTermsAccepted())
                .openAiApiKey(request.getOpenAiApiKey() != null
                        ? request.getOpenAiApiKey() : existing.getOpenAiApiKey())
                .jsearchApiKey(request.getJsearchApiKey() != null
                        ? request.getJsearchApiKey() : existing.getJsearchApiKey())
                .build();

        return toResponse(userSettingsRepository.save(patched));
    }

    private UserSettings emptySettings(Long userId) {
        return UserSettings.builder()
                .userId(userId)
                .termsAccepted(false)
                .build();
    }

    private UserSettingsResponse toResponse(UserSettings settings) {
        boolean hasOpenAi = settings.getOpenAiApiKey() != null && !settings.getOpenAiApiKey().isBlank();
        boolean hasJsearch = settings.getJsearchApiKey() != null && !settings.getJsearchApiKey().isBlank();

        return UserSettingsResponse.builder()
                .language(settings.getLanguage())
                .theme(settings.getTheme())
                .termsAccepted(settings.isTermsAccepted())
                .hasOpenAiApiKey(hasOpenAi)
                .hasJsearchApiKey(hasJsearch)
                .openAiApiKeyMasked(hasOpenAi ? encryptionService.mask(settings.getOpenAiApiKey()) : null)
                .jsearchApiKeyMasked(hasJsearch ? encryptionService.mask(settings.getJsearchApiKey()) : null)
                .updatedAt(settings.getUpdatedAt() != null ? settings.getUpdatedAt().toString() : null)
                .build();
    }
}
