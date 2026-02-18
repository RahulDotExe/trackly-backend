package com.trackly.trackly_backend.user.service;

import com.trackly.trackly_backend.user.entity.RefreshToken;
import com.trackly.trackly_backend.user.entity.User;
import com.trackly.trackly_backend.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${refresh-token.expiration-days}")
    private long refreshTokenExpirationDays;

    //Public  API
    public String createRefreshToken(User user){
        String rawToken = generateSecureToken();
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(tokenHash);
        refreshToken.setExpiresAt(
                Instant.now().plus(Duration.ofDays(refreshTokenExpirationDays))
        );
        refreshToken.setRevoked(false);
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    // Generate Token

    public String generateSecureToken(){
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // Hashing

    public String hashToken(String token){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b:hashBytes){
                String hex = Integer.toHexString(0xff & b);
                if (hex.length()== 1) hexString.append('0');
                hexString.append(hex);

            }
            return hexString.toString();

            //Modern Java 17+ way:
            //        return HexFormat.of().formatHex(hashBytes);

        } catch (NoSuchAlgorithmException e){
            throw new IllegalStateException("SHA-256 algorithm not found",e);
        }
    }
}
