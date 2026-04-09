package com.searchjobs.api.domain.port.in;

import com.searchjobs.api.application.dto.request.*;
import com.searchjobs.api.application.dto.response.*;

import java.util.List;

public interface UserProfileUseCase {
    UserProfileResponse getProfile(Long userId);
    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);

    List<UserSkillResponse> getSkills(Long userId);
    List<UserSkillResponse> updateSkills(Long userId, UpdateSkillsRequest request);

    List<UserExperienceResponse> getExperiences(Long userId);
    List<UserExperienceResponse> updateExperiences(Long userId, UpdateExperiencesRequest request);

    List<UserCertificationResponse> getCertifications(Long userId);
    List<UserCertificationResponse> updateCertifications(Long userId, UpdateCertificationsRequest request);

    List<UserProjectResponse> getProjects(Long userId);
    List<UserProjectResponse> updateProjects(Long userId, UpdateProjectsRequest request);
}