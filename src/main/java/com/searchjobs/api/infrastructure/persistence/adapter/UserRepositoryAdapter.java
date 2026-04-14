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
        UserJpaEntity entity;

        if (user.getId() != null) {
            entity = jpaRepository.findById(user.getId())
                    .orElse(new UserJpaEntity());
        } else {
            entity = jpaRepository.findByEmail(user.getEmail())
                    .orElse(new UserJpaEntity());
        }

        entity.setNome(user.getNome());
        entity.setEmail(user.getEmail());
        entity.setSenhaHash(user.getSenhaHash());

        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
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