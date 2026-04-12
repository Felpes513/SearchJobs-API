package com.searchjobs.api.infrastructure.persistence.adapter;

import com.searchjobs.api.domain.model.UserSettings;
import com.searchjobs.api.domain.port.out.UserSettingsRepository;
import com.searchjobs.api.infrastructure.encryption.EncryptionService;
import com.searchjobs.api.infrastructure.persistence.entity.UserSettingsJpaEntity;
import com.searchjobs.api.infrastructure.persistence.repository.UserSettingsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserSettingsRepositoryAdapter implements UserSettingsRepository {

    private final UserSettingsJpaRepository jpaRepository;
    private final EncryptionService encryptionService;

    @Override
    public Optional<UserSettings> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public UserSettings save(UserSettings settings) {
        UserSettingsJpaEntity entity = jpaRepository.findByUserId(settings.getUserId())
                .orElse(new UserSettingsJpaEntity());

        entity.setUserId(settings.getUserId());
        entity.setLanguage(settings.getLanguage());
        entity.setTheme(settings.getTheme());
        entity.setTermsAccepted(settings.isTermsAccepted());

        if (settings.getOpenAiApiKey() != null) {
            entity.setOpenaiApiKey(encryptionService.encrypt(settings.getOpenAiApiKey()));
        }
        if (settings.getJsearchApiKey() != null) {
            entity.setJsearchApiKey(encryptionService.encrypt(settings.getJsearchApiKey()));
        }

        return toDomain(jpaRepository.save(entity));
    }

    private UserSettings toDomain(UserSettingsJpaEntity entity) {
        return UserSettings.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .language(entity.getLanguage())
                .theme(entity.getTheme())
                .termsAccepted(entity.isTermsAccepted())
                .openAiApiKey(encryptionService.decrypt(entity.getOpenaiApiKey()))
                .jsearchApiKey(encryptionService.decrypt(entity.getJsearchApiKey()))
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
