package com.searchjobs.api.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", length = 150)
    private String externalId;

    @Column(name = "source_id", nullable = false)
    private Long sourceId;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "empresa", length = 150)
    private String empresa;

    @Column(name = "localizacao", length = 150)
    private String localizacao;

    @Column(name = "modelo_trabalho", length = 20, nullable = false)
    private String modeloTrabalho = "NAO_INFORMADO";

    @Column(name = "senioridade", length = 20, nullable = false)
    private String senioridade = "NAO_INFORMADO";

    @Column(name = "descricao", columnDefinition = "text")
    private String descricao;

    @Column(name = "requisitos", columnDefinition = "text")
    private String requisitos;

    @Column(name = "salario", length = 100)
    private String salario;

    @Column(name = "job_url", nullable = false, length = 500)
    private String jobUrl;

    @Column(name = "data_publicacao")
    private LocalDateTime dataPublicacao;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}