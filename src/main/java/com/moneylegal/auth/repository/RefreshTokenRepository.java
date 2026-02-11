package com.moneylegal.auth.repository;

import com.moneylegal.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para RefreshToken
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    /**
     * Buscar refresh token válido
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.token = :token AND rt.revoked = false AND rt.expiresAt > :now")
    Optional<RefreshToken> findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);

    /**
     * Buscar por token
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Buscar todos os tokens do usuário
     */
    List<RefreshToken> findByUserId(String userId);

    /**
     * Buscar tokens válidos do usuário
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.userId = :userId AND rt.revoked = false AND rt.expiresAt > :now")
    List<RefreshToken> findValidTokensByUserId(@Param("userId") String userId, @Param("now") LocalDateTime now);

    /**
     * Revogar todos os tokens do usuário
     */
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :now WHERE rt.userId = :userId AND rt.revoked = false")
    int revokeAllUserTokens(@Param("userId") String userId, @Param("now") LocalDateTime now);

    /**
     * Deletar tokens expirados
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);
}
