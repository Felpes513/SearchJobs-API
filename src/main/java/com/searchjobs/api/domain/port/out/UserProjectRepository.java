package com.searchjobs.api.domain.port.out;

import com.searchjobs.api.domain.model.UserProject;
import java.util.List;

public interface UserProjectRepository {
    void saveAll(List<UserProject> projects);
    void deleteByUserId(Long userId);
}