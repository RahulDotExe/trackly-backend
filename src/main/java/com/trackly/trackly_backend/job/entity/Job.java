package com.trackly.trackly_backend.job.entity;


import com.trackly.trackly_backend.common.entity.BaseEntity;
import com.trackly.trackly_backend.job.enums.JobStatus;
import com.trackly.trackly_backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "jobs")
@Builder
@Setter
public class Job extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    private String location;

    private String jobUrl;

    @Column(length = 2000)
    private String notes;

    private Instant appliedAt;


}
