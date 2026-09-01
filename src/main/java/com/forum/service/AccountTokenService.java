package com.forum.service;

import com.forum.entity.AccountToken;
import com.forum.entity.AccountTokenType;
import com.forum.entity.User;
import com.forum.exception.AuthenticationFailedException;
import com.forum.repository.AccountTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountTokenService {
    private final AccountTokenRepository accountTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public String create(User user, AccountTokenType type, long expiryMinutes) {
        accountTokenRepository.deleteByUserAndType(user, type);
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        AccountToken token = new AccountToken();
        token.setUser(user);
        token.setType(type);
        token.setTokenHash(hash(rawToken));
        token.setExpiresAt(LocalDateTime.now().plusMinutes(expiryMinutes));
        accountTokenRepository.save(token);
        return rawToken;
    }

    public User consume(String rawToken, AccountTokenType type) {
        AccountToken token = accountTokenRepository.findByTokenHashAndType(hash(rawToken), type)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid or expired token"));
        if (token.getUsedAt() != null || !token.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new AuthenticationFailedException("Invalid or expired token");
        }
        token.setUsedAt(LocalDateTime.now());
        return token.getUser();
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
