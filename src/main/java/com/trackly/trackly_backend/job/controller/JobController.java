package com.trackly.trackly_backend.job.controller;

import com.trackly.trackly_backend.job.dto.CreateJobRequest;
import com.trackly.trackly_backend.job.dto.JobResponse;
import com.trackly.trackly_backend.job.service.JobService;
import com.trackly.trackly_backend.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateJobRequest request
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        jobService.createJob(
                                currentUser.getId(),
                                request
                        )
                );
    }
}
