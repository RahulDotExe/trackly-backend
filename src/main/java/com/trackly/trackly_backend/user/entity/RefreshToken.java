package com.trackly.trackly_backend.user.entity;

import com.trackly.trackly_backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name="idx_token_hash",columnList = "token_hash"),
                @Index(name = "idx_user_id", columnList = "user_id")

        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_token_hash",columnNames = "token_hash")
        }
)
public class RefreshToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked = false;




}
