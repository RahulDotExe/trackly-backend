package com.trackly.trackly_backend.job.exceptions;

public class JobNotFoundException extends RuntimeException{

    public JobNotFoundException() {
        super("Job not found");
    }
}
