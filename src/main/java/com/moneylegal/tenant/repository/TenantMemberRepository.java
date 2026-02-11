package com.moneylegal.tenant.repository;

import com.moneylegal.tenant.entity.TenantMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para TenantMember
 */
@Repository
public interface TenantMemberRepository extends JpaRepository<TenantMember, String> {

    /**
     * Buscar membership específico
     */
    Optional<TenantMember> findByTenantIdAndUserId(String tenantId, String userId);

    /**
     * Buscar todos os tenants de um usuário
     */
    List<TenantMember> findByUserId(String userId);

    /**
     * Buscar todos os tenants ativos de um usuário
     */
    List<TenantMember> findByUserIdAndIsActiveTrue(String userId);

    /**
     * Buscar todos os membros de um tenant
     */
    List<TenantMember> findByTenantId(String tenantId);

    /**
     * Buscar membros ativos de um tenant
     */
    List<TenantMember> findByTenantIdAndIsActiveTrue(String tenantId);

    /**
     * Buscar membros por role
     */
    List<TenantMember> findByTenantIdAndRole(String tenantId, TenantMember.MemberRole role);

    /**
     * Verificar se usuário é membro do tenant
     */
    boolean existsByTenantIdAndUserId(String tenantId, String userId);

    /**
     * Verificar se usuário é membro ativo do tenant
     */
    boolean existsByTenantIdAndUserIdAndIsActiveTrue(String tenantId, String userId);

    /**
     * Contar membros do tenant
     */
    long countByTenantId(String tenantId);

    /**
     * Contar membros ativos do tenant
     */
    long countByTenantIdAndIsActiveTrue(String tenantId);

    /**
     * Buscar owner do tenant
     */
    @Query("SELECT tm FROM TenantMember tm WHERE tm.tenantId = :tenantId AND tm.role = 'OWNER'")
    Optional<TenantMember> findOwner(@Param("tenantId") String tenantId);

    /**
     * Buscar admins do tenant
     */
    @Query("SELECT tm FROM TenantMember tm WHERE tm.tenantId = :tenantId AND tm.role IN ('ADMIN', 'OWNER')")
    List<TenantMember> findAdmins(@Param("tenantId") String tenantId);

    /**
     * Verificar se usuário tem role mínima
     */
    @Query("SELECT CASE WHEN COUNT(tm) > 0 THEN true ELSE false END FROM TenantMember tm " +
           "WHERE tm.tenantId = :tenantId AND tm.userId = :userId AND tm.isActive = true " +
           "AND (tm.role = :role OR tm.role = 'ADMIN' OR tm.role = 'OWNER')")
    boolean hasMinimumRole(
        @Param("tenantId") String tenantId, 
        @Param("userId") String userId, 
        @Param("role") TenantMember.MemberRole role
    );
}
