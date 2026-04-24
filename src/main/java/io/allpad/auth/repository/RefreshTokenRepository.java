package io.allpad.auth.repository;

import io.allpad.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenAndUsername(String token, String username);

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByExpirationBefore(long now);
}
