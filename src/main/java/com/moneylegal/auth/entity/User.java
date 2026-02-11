package com.moneylegal.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

/**
 * Entidade User - Representa um usuário do sistema
 * 
 * Um usuário pode:
 * - Fazer login no sistema
 * - Pertencer a múltiplos tenants
 * - Ter diferentes roles em diferentes tenants
 * - Login via email/senha ou OAuth (Google, Facebook, Apple)
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email"),
    @Index(name = "idx_users_phone", columnList = "phone"),
    @Index(name = "idx_users_is_active", columnList = "is_active"),
    @Index(name = "idx_users_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(length = 20)
    private String phone;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "phone_verified", nullable = false)
    @Builder.Default
    private Boolean phoneVerified = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    /**
     * OAuth2 Fields
     */
    @Column(name = "oauth_provider", length = 20)
    private String oauthProvider; // GOOGLE, FACEBOOK, APPLE

    @Column(name = "oauth_provider_id", length = 255)
    private String oauthProviderId;

    /**
     * OTP/Reset
     */
    @Column(name = "reset_password_code", length = 6)
    private String resetPasswordCode;

    @Column(name = "reset_password_code_expires_at")
    private LocalDateTime resetPasswordCodeExpiresAt;

    /**
     * Lifecycle hooks
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business methods
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public void verifyPhone() {
        this.phoneVerified = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public boolean isOAuthUser() {
        return oauthProvider != null && oauthProviderId != null;
    }

    public boolean canLogin() {
        return isActive && emailVerified;
    }

    // Getters e Setters
    public String getResetPasswordCode() { return resetPasswordCode; }

    public void setResetPasswordCode(String resetPasswordCode) { this.resetPasswordCode = resetPasswordCode; }

    public LocalDateTime getResetPasswordCodeExpiresAt() { return resetPasswordCodeExpiresAt; }

    public void setResetPasswordCodeExpiresAt(LocalDateTime resetPasswordCodeExpiresAt) { this.resetPasswordCodeExpiresAt = resetPasswordCodeExpiresAt; }
}
