package com.trackly.trackly_backend.job.service;

import com.trackly.trackly_backend.job.dto.CreateJobRequest;
import com.trackly.trackly_backend.job.dto.JobResponse;
import com.trackly.trackly_backend.job.entity.Job;
import com.trackly.trackly_backend.job.enums.JobStatus;
import com.trackly.trackly_backend.job.repository.JobRepository;
import com.trackly.trackly_backend.user.entity.User;
import com.trackly.trackly_backend.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public JobResponse createJob(UUID userId, CreateJobRequest request){

        // Find the authenticated user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Job job = Job.builder()
                .user(user)
                .companyName(request.getCompanyName())
                .jobTitle(request.getJobTitle())
                .location(request.getLocation())
                .jobUrl(request.getJobUrl())
                .notes(request.getNotes())
                .status(JobStatus.SAVED)
                .build();

            Job savedJob = jobRepository.save(job);

        return JobResponse.builder()
                .id(savedJob.getId())
                .companyName(savedJob.getCompanyName())
                .jobTitle(savedJob.getJobTitle())
                .location(savedJob.getLocation())
                .status(savedJob.getStatus())
                .jobUrl(savedJob.getJobUrl())
                .notes(savedJob.getNotes())
                .createdAt(savedJob.getCreatedAt())
                .updatedAt(savedJob.getUpdatedAt())
                .appliedAt(savedJob.getAppliedAt())
                .build();


    }



}
