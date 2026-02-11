package com.moneylegal.tenant.repository;

import com.moneylegal.tenant.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
    
    Optional<Tenant> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
    
    /**
     * Buscar todos os tenants ativos (para listagem pública)
     */
    Page<Tenant> findAllByIsActiveTrue(Pageable pageable);
    
    /**
     * Buscar tenants ativos com filtro de nome ou slug
     */
    @Query("SELECT t FROM Tenant t WHERE t.isActive = true AND " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.slug) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Tenant> findAllActiveByNameOrSlug(@Param("search") String search, Pageable pageable);
    
    /**
     * Buscar tenants do usuário com filtro de nome ou slug
     */
    @Query("SELECT DISTINCT t FROM Tenant t " +
           "JOIN TenantMember m ON m.tenantId = t.id " +
           "WHERE m.userId = :userId AND m.isActive = true AND t.isActive = true AND " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.slug) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY t.createdAt DESC")
    List<Tenant> findByUserIdAndNameOrSlug(
        @Param("userId") String userId, 
        @Param("search") String search
    );
    
    /**
     * Buscar todos os tenants do usuário
     */
    @Query("SELECT DISTINCT t FROM Tenant t " +
           "JOIN TenantMember m ON m.tenantId = t.id " +
           "WHERE m.userId = :userId AND m.isActive = true AND t.isActive = true " +
           "ORDER BY t.createdAt DESC")
    List<Tenant> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);
}
