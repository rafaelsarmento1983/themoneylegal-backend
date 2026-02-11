package com.moneylegal.tenant.repository;

import com.moneylegal.tenant.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para Invitation
 */
@Repository
public interface InvitationRepository extends JpaRepository<Invitation, String> {

    /**
     * Buscar convite por código
     */
    Optional<Invitation> findByCode(String code);

    /**
     * Buscar convite válido por código
     */
    @Query("SELECT i FROM Invitation i WHERE i.code = :code AND i.status = 'PENDING' AND i.expiresAt > :now")
    Optional<Invitation> findValidByCode(@Param("code") String code, @Param("now") LocalDateTime now);

    /**
     * Buscar convites do tenant
     */
    List<Invitation> findByTenantId(String tenantId);

    /**
     * Buscar convites pendentes do tenant
     */
    List<Invitation> findByTenantIdAndStatus(String tenantId, Invitation.InvitationStatus status);

    /**
     * Buscar convites por email
     */
    List<Invitation> findByEmail(String email);

    /**
     * Buscar convites pendentes por email
     */
    @Query("SELECT i FROM Invitation i WHERE i.email = :email AND i.status = 'PENDING' AND i.expiresAt > :now")
    List<Invitation> findPendingByEmail(@Param("email") String email, @Param("now") LocalDateTime now);

    /**
     * Verificar se email já foi convidado para o tenant (pendente)
     */
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Invitation i " +
           "WHERE i.tenantId = :tenantId AND i.email = :email AND i.status = 'PENDING'")
    boolean existsPendingInvitation(@Param("tenantId") String tenantId, @Param("email") String email);

    /**
     * Expirar convites antigos
     */
    @Modifying
    @Query("UPDATE Invitation i SET i.status = 'EXPIRED' WHERE i.status = 'PENDING' AND i.expiresAt < :now")
    int expireOldInvitations(@Param("now") LocalDateTime now);

    /**
     * Contar convites pendentes do tenant
     */
    long countByTenantIdAndStatus(String tenantId, Invitation.InvitationStatus status);
}
