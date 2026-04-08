package com.searchjobs.api.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String nome;
    private String email;
    private String senhaHash;
    private LocalDateTime createdAt;
}