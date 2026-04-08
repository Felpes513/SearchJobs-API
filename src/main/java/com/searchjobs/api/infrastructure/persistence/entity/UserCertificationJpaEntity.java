package com.searchjobs.api.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_certifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCertificationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "nome_certificacao", nullable = false, length = 150)
    private String nomeCertificacao;

    @Column(name = "instituicao", length = 150)
    private String instituicao;

    @Column(name = "data_obtencao")
    private String dataObtencao;
}