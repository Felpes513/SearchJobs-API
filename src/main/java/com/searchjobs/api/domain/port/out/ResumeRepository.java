package com.searchjobs.api.domain.port.out;

import com.searchjobs.api.domain.model.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository {
    Resume save(Resume resume);
    Optional<Resume> findById(Long id);
    Resume update(Resume resume);
    List<Resume> findAllByUserId(Long userId);
    Page<Resume> findAllByUserId(Long userId, Pageable pageable);
    void deleteById(Long id);
    Optional<Resume> findByUserId(Long userId);
}