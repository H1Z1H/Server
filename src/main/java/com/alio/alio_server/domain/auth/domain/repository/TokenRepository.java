package com.alio.alio_server.domain.auth.domain.repository;


import com.alio.alio_server.domain.auth.domain.Token;
import com.alio.alio_server.domain.user.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUser(Users user);

    Optional<Token> findByRefreshToken(String refreshToken);
}
