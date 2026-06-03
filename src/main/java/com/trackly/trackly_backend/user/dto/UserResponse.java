package com.trackly.trackly_backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
 public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private Instant createdAt;

}
