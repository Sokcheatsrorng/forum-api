package com.forum.repository;

import com.forum.entity.AccountToken;
import com.forum.entity.AccountTokenType;
import com.forum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountTokenRepository extends JpaRepository<AccountToken, Long> {
    Optional<AccountToken> findByTokenHashAndType(String tokenHash, AccountTokenType type);

    void deleteByUserAndType(User user, AccountTokenType type);
}
