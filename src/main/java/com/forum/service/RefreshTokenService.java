package com.forum.service;

import com.forum.entity.RefreshToken;
import com.forum.entity.User;
import com.forum.exception.AuthenticationFailedException;
import com.forum.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.security.jwt.refresh-expiration:2592000000}")
    private long refreshExpirationMs;

    public String create(User user) {
        String rawToken = UUID.randomUUID() + "." + UUID.randomUUID();
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenHash(hash(rawToken));
        token.setExpiresAt(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000));
        refreshTokenRepository.save(token);
        return rawToken;
    }

    /** A refresh token can be used once. Rotation limits the impact of a stolen token. */
    public User consume(String rawToken) {
        RefreshToken token = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new AuthenticationFailedException("Invalid refresh token"));
        if (token.isRevoked() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AuthenticationFailedException("Refresh token has expired or was revoked");
        }
        token.setRevoked(true);
        return token.getUser();
    }

    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHash(hash(rawToken)).ifPresent(token -> token.setRevoked(true));
    }

    public void revokeAll(User user) {
        refreshTokenRepository.findByUser(user).stream()
                .forEach(token -> token.setRevoked(true));
    }

    private String hash(String value) {
        try {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(
                    MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }
}
