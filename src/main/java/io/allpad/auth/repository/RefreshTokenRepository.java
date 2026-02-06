package io.allpad.auth.repository;

import io.allpad.auth.entity.RefreshToken;
import io.allpad.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenAndUser(String token, User user);

    Optional<RefreshToken> findByToken(String token);
}
