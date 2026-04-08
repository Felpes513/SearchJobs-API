package com.searchjobs.api.domain.port.out;

import com.searchjobs.api.domain.model.UserCertification;
import java.util.List;

public interface UserCertificationRepository {
    void saveAll(List<UserCertification> certifications);
    void deleteByUserId(Long userId);
}