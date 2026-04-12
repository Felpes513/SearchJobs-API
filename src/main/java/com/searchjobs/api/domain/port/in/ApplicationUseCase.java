package com.searchjobs.api.domain.port.in;

import com.searchjobs.api.application.dto.request.CreateApplicationRequest;
import com.searchjobs.api.application.dto.request.UpdateApplicationStatusRequest;
import com.searchjobs.api.application.dto.response.ApplicationKanbanResponse;

public interface ApplicationUseCase {
    ApplicationKanbanResponse getKanban(Long userId);
    void create(Long userId, CreateApplicationRequest request);
    void updateStatus(Long userId, Long applicationId, UpdateApplicationStatusRequest request);
    void delete(Long userId, Long applicationId);
}