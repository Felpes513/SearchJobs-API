package com.searchjobs.api.domain.port.in;

import com.searchjobs.api.application.dto.response.ResumeListResponse;
import com.searchjobs.api.application.dto.response.ResumeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ResumeUseCase {
    ResumeResponse upload(Long userId, MultipartFile file);
    Page<ResumeListResponse> findAllByUser(Long userId, Pageable pageable);
    void delete(Long userId, Long resumeId);
}
