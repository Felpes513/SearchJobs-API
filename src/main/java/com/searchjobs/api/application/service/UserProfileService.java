package com.searchjobs.api.application.service;

import com.searchjobs.api.application.dto.request.*;
import com.searchjobs.api.application.dto.response.*;
import com.searchjobs.api.domain.exception.ResumeNotFoundException;
import com.searchjobs.api.domain.model.*;
import com.searchjobs.api.domain.port.in.UserProfileUseCase;
import com.searchjobs.api.domain.port.out.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService implements UserProfileUseCase {

    private final UserProfileRepository userProfileRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserExperienceRepository userExperienceRepository;
    private final UserCertificationRepository userCertificationRepository;
    private final UserProjectRepository userProjectRepository;

    @Override
    public UserProfileResponse getProfile(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado"));
        return toProfileResponse(profile);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        UserProfile existing = userProfileRepository.findByUserId(userId)
                .orElse(UserProfile.builder().userId(userId).build());

        UserProfile updated = UserProfile.builder()
                .id(existing.getId())
                .userId(userId)
                .resumoProfissional(request.getResumoProfissional())
                .cargoDesejado(request.getCargoDesejado())
                .cidade(request.getCidade())
                .estado(request.getEstado())
                .linkedinUrl(request.getLinkedinUrl())
                .githubUrl(request.getGithubUrl())
                .build();

        return toProfileResponse(userProfileRepository.save(updated));
    }

    @Override
    public List<UserSkillResponse> getSkills(Long userId) {
        return userSkillRepository.findAllByUserId(userId)
                .stream()
                .map(s -> UserSkillResponse.builder()
                        .id(s.getId())
                        .nomeSkill(s.getNomeSkill())
                        .nivel(s.getNivel())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public List<UserSkillResponse> updateSkills(Long userId, UpdateSkillsRequest request) {
        userSkillRepository.deleteByUserId(userId);
        userSkillRepository.saveAll(userId, request.getSkills());
        return getSkills(userId);
    }

    @Override
    public List<UserExperienceResponse> getExperiences(Long userId) {
        return userExperienceRepository.findAllByUserId(userId)
                .stream()
                .map(e -> UserExperienceResponse.builder()
                        .id(e.getId())
                        .cargo(e.getCargo())
                        .empresa(e.getEmpresa())
                        .descricao(e.getDescricao())
                        .dataInicio(e.getDataInicio())
                        .dataFim(e.getDataFim())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public List<UserExperienceResponse> updateExperiences(Long userId, UpdateExperiencesRequest request) {
        userExperienceRepository.deleteByUserId(userId);
        List<UserExperience> experiences = request.getExperiencias().stream()
                .map(e -> UserExperience.builder()
                        .userId(userId)
                        .cargo(e.getCargo())
                        .empresa(e.getEmpresa())
                        .descricao(e.getDescricao())
                        .dataInicio(e.getDataInicio())
                        .dataFim(e.getDataFim())
                        .build())
                .toList();
        userExperienceRepository.saveAll(experiences);
        return getExperiences(userId);
    }

    @Override
    public List<UserCertificationResponse> getCertifications(Long userId) {
        return userCertificationRepository.findAllByUserId(userId)
                .stream()
                .map(c -> UserCertificationResponse.builder()
                        .id(c.getId())
                        .nomeCertificacao(c.getNomeCertificacao())
                        .instituicao(c.getInstituicao())
                        .dataObtencao(c.getDataObtencao())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public List<UserCertificationResponse> updateCertifications(Long userId, UpdateCertificationsRequest request) {
        userCertificationRepository.deleteByUserId(userId);
        List<UserCertification> certifications = request.getCertificacoes().stream()
                .map(c -> UserCertification.builder()
                        .userId(userId)
                        .nomeCertificacao(c.getNomeCertificacao())
                        .instituicao(c.getInstituicao())
                        .dataObtencao(c.getDataObtencao())
                        .build())
                .toList();
        userCertificationRepository.saveAll(certifications);
        return getCertifications(userId);
    }

    @Override
    public List<UserProjectResponse> getProjects(Long userId) {
        return userProjectRepository.findAllByUserId(userId)
                .stream()
                .map(p -> UserProjectResponse.builder()
                        .id(p.getId())
                        .nome(p.getNome())
                        .descricao(p.getDescricao())
                        .stack(p.getStack())
                        .link(p.getLink())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public List<UserProjectResponse> updateProjects(Long userId, UpdateProjectsRequest request) {
        userProjectRepository.deleteByUserId(userId);
        List<UserProject> projects = request.getProjetos().stream()
                .map(p -> UserProject.builder()
                        .userId(userId)
                        .nome(p.getNome())
                        .descricao(p.getDescricao())
                        .stack(p.getStack())
                        .link(p.getLink())
                        .build())
                .toList();
        userProjectRepository.saveAll(projects);
        return getProjects(userId);
    }

    private UserProfileResponse toProfileResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .id(profile.getId())
                .resumoProfissional(profile.getResumoProfissional())
                .cargoDesejado(profile.getCargoDesejado())
                .cidade(profile.getCidade())
                .estado(profile.getEstado())
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .build();
    }
}