package com.moneylegal.tenant.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

/**
 * Entidade TenantMember - Relacionamento entre User e Tenant
 * 
 * Roles hierárquicas (do menor para o maior privilégio):
 * - VIEWER: Apenas visualização
 * - MEMBER: VIEWER + criar transações próprias
 * - MANAGER: MEMBER + aprovar, gerenciar categorias/budgets
 * - ADMIN: MANAGER + convidar/remover membros, configurações
 * - OWNER: ADMIN + deletar tenant, alterar plano
 */
@Entity
@Table(name = "tenant_members", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_tenant_members", columnNames = {"tenant_id", "user_id"})
    },
    indexes = {
        @Index(name = "idx_tenant_members_tenant", columnList = "tenant_id"),
        @Index(name = "idx_tenant_members_user", columnList = "user_id"),
        @Index(name = "idx_tenant_members_role", columnList = "role")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantMember {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MemberRole role = MemberRole.MEMBER;

    @Column(name = "invited_by", length = 36)
    private String invitedBy;

    @Column(name = "joined_at", nullable = false)
    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Business methods
     */
    public void promote(MemberRole newRole) {
        if (newRole.getLevel() > this.role.getLevel()) {
            this.role = newRole;
        }
    }

    public void demote(MemberRole newRole) {
        if (newRole.getLevel() < this.role.getLevel() && this.role != MemberRole.OWNER) {
            this.role = newRole;
        }
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public boolean isOwner() {
        return role == MemberRole.OWNER;
    }

    public boolean isAdmin() {
        return role == MemberRole.ADMIN || isOwner();
    }

    public boolean isManager() {
        return role == MemberRole.MANAGER || isAdmin();
    }

    public boolean canView() {
        return isActive;
    }

    public boolean canEdit() {
        return isActive && (role == MemberRole.MEMBER || isManager());
    }

    public boolean canApprove() {
        return isActive && isManager();
    }

    public boolean canManageMembers() {
        return isActive && isAdmin();
    }

    public boolean canManageTenant() {
        return isActive && isOwner();
    }

    /**
     * Enum de Roles com níveis hierárquicos
     */
    @Getter
    public enum MemberRole {
        VIEWER(1),
        MEMBER(2),
        MANAGER(3),
        ADMIN(4),
        OWNER(5);
        private final int level;
        MemberRole(int level) {
            this.level = level;
        }

        public boolean hasPermission(MemberRole requiredRole) {
            return this.level >= requiredRole.level;
        }
    }
}
