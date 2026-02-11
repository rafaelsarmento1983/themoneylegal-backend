package com.moneylegal.tenant.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

/**
 * Entidade Invitation - Convite para ingressar em um tenant
 * 
 * Fluxo:
 * 1. Admin/Owner convida alguém (email)
 * 2. Sistema gera código único (6 caracteres)
 * 3. Email enviado com código
 * 4. Usuário usa código para aceitar convite
 * 5. Convite expira em 7 dias
 */
@Entity
@Table(name = "invitations", indexes = {
    @Index(name = "idx_invitations_tenant", columnList = "tenant_id"),
    @Index(name = "idx_invitations_email", columnList = "email"),
    @Index(name = "idx_invitations_code", columnList = "code", unique = true),
    @Index(name = "idx_invitations_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitation {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TenantMember.MemberRole role = TenantMember.MemberRole.MEMBER;

    @Column(name = "invited_by", nullable = false, length = 36)
    private String invitedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private InvitationStatus status = InvitationStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "accepted_by", length = 36)
    private String acceptedBy;

    /**
     * Business methods
     */
    public boolean isValid() {
        return status == InvitationStatus.PENDING 
            && expiresAt.isAfter(LocalDateTime.now());
    }

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public void accept(String userId) {
        this.status = InvitationStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
        this.acceptedBy = userId;
    }

    public void reject() {
        this.status = InvitationStatus.REJECTED;
        this.rejectedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = InvitationStatus.CANCELLED;
    }

    public void expire() {
        this.status = InvitationStatus.EXPIRED;
    }

    /**
     * Factory method
     */
    public static Invitation create(
        String tenantId, 
        String email, 
        String code, 
        TenantMember.MemberRole role, 
        String invitedBy,
        int expirationDays
    ) {
        return Invitation.builder()
            .tenantId(tenantId)
            .email(email)
            .code(code)
            .role(role)
            .invitedBy(invitedBy)
            .expiresAt(LocalDateTime.now().plusDays(expirationDays))
            .build();
    }

    /**
     * Enum de Status
     */
    public enum InvitationStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        CANCELLED,
        EXPIRED
    }
}
