package com.moneylegal.auth.repository;

import com.moneylegal.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para User
 * 
 * Queries customizadas para:
 * - Buscar por email/phone
 * - Verificar existência
 * - Buscar usuários OAuth
 * - Buscar usuários ativos
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Buscar usuário por email
     */
    Optional<User> findByEmail(String email);

    /**
     * Buscar usuário por email (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(@Param("email") String email);

    /**
     * Buscar usuário por telefone
     */
    Optional<User> findByPhone(String phone);

    /**
     * Verificar se email existe
     */
    boolean existsByEmail(String email);

    /**
     * Verificar se email existe (case insensitive)
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);

    /**
     * Verificar se telefone existe
     */
    boolean existsByPhone(String phone);

    /**
     * Buscar usuário OAuth
     */
    Optional<User> findByOauthProviderAndOauthProviderId(String oauthProvider, String oauthProviderId);

    /**
     * Buscar usuários ativos
     */
    List<User> findByIsActiveTrue();

    /**
     * Buscar usuários inativos
     */
    List<User> findByIsActiveFalse();

    /**
     * Buscar usuários com email verificado
     */
    List<User> findByEmailVerifiedTrue();

    /**
     * Buscar usuários com email não verificado
     */
    List<User> findByEmailVerifiedFalse();

    /**
     * Buscar usuários criados após uma data
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Buscar usuários que fizeram login recentemente
     */
    @Query("SELECT u FROM User u WHERE u.lastLogin >= :since ORDER BY u.lastLogin DESC")
    List<User> findRecentlyActive(@Param("since") LocalDateTime since);

    /**
     * Buscar usuários por nome (like)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> searchByName(@Param("name") String name);

    /**
     * Contar usuários ativos
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();

    /**
     * Contar usuários por provider OAuth
     */
    long countByOauthProvider(String oauthProvider);

    /**
     * Verifica se existe usuário com o email E com senha definida (cadastro completo)
     */
    boolean existsByEmailIgnoreCaseAndPasswordHashIsNotNull(String email);
}
