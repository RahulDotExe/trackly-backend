package com.trackly.trackly_backend.user.repository;

import com.trackly.trackly_backend.user.entity.RefreshToken;
import com.trackly.trackly_backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findAllByUser(User user);
    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);

}
