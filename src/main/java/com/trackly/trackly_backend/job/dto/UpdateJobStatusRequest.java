package com.trackly.trackly_backend.job.dto;

import com.trackly.trackly_backend.job.enums.JobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateJobStatusRequest {
    @NotNull
    private JobStatus status;
}
