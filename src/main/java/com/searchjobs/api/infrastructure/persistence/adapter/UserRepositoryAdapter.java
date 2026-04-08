package com.searchjobs.api.infrastructure.persistence.adapter;

import com.searchjobs.api.domain.model.User;
import com.searchjobs.api.domain.port.out.UserRepository;
import com.searchjobs.api.infrastructure.persistence.entity.UserJpaEntity;
import com.searchjobs.api.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserJpaEntity.builder()
                .nome(user.getNome())
                .email(user.getEmail())
                .senhaHash(user.getSenhaHash())
                .build();

        UserJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    private User toDomain(UserJpaEntity entity) {
        return User.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .senhaHash(entity.getSenhaHash())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}