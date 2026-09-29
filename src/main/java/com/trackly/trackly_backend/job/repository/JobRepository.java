package com.trackly.trackly_backend.job.repository;

import com.trackly.trackly_backend.job.entity.Job;
import com.trackly.trackly_backend.job.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;


public interface JobRepository extends JpaRepository<Job, UUID> {

    Optional<Job> findByIdAndUserId(UUID jobId, UUID userId);

    Page<Job> findByUserId(UUID userId, Pageable pageable);

    Page<Job> findByUserIdAndStatus(UUID userId, JobStatus status, Pageable pageable);
}
