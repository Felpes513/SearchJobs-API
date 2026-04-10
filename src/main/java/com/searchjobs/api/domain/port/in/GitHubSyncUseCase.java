package com.searchjobs.api.domain.port.in;

import com.searchjobs.api.application.dto.response.UserProjectResponse;
import java.util.List;

public interface GitHubSyncUseCase {
    List<UserProjectResponse> sync(Long userId);
}