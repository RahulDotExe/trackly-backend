package com.trackly.trackly_backend.job.dto;

import com.trackly.trackly_backend.job.enums.JobStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobResponse {

    private UUID id;

    private String companyName;

    private  String jobTitle;

    private String location;

    private JobStatus status;

    private String jobUrl;

    private String notes;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant appliedAt;



}
