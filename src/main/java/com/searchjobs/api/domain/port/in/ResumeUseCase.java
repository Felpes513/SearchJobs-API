package com.searchjobs.api.domain.port.in;

import com.searchjobs.api.application.dto.response.ResumeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResumeUseCase {
    ResumeResponse upload(Long userId, MultipartFile file);
    List<ResumeResponse> findAllByUser(Long userId);
    Page<ResumeResponse> findAllByUser(Long userId, Pageable pageable);
    void delete(Long userId, Long resumeId);
}
