package com.moneylegal.tenant.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug; // ✅ NOVO

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TenantType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false)
    private TenantPlan plan;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "primary_color")
    private String primaryColor;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "subscription_start_date")
    private LocalDate subscriptionStartDate;

    @Column(name = "subscription_end_date")
    private LocalDate subscriptionEndDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum SubscriptionStatus {
        ACTIVE, INACTIVE, TRIAL, CANCELLED
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status")
    private SubscriptionStatus subscriptionStatus;

    @Column(name = "subscription_expires_at")
    private LocalDateTime subscriptionExpiresAt;

    @Column(name = "max_members")
    private Integer maxMembers;

    @Column(name = "max_accounts")
    private Integer maxAccounts;

    @Column(name = "max_budgets")
    private Integer maxBudgets;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum TenantType {
        PERSONAL, FAMILY, BUSINESS
    }

    public enum TenantPlan {
        FREE, PREMIUM, ENTERPRISE
    }
    /*public void startSubscription(TenantPlan plan, int daysValidity) {
        this.plan = plan;
        this.subscriptionStartDate = LocalDate.now();
        this.subscriptionEndDate = LocalDate.now().plusDays(daysValidity);
    }*/

    public void startSubscription(TenantPlan plan, int daysValidity) {
        this.plan = (plan != null) ? plan : TenantPlan.FREE;

        this.subscriptionStartDate = LocalDate.now();
        this.subscriptionEndDate = LocalDate.now().plusDays(Math.max(daysValidity, 0));

        if (this.subscriptionStatus == null) {
            this.subscriptionStatus = SubscriptionStatus.ACTIVE;
        }

        this.subscriptionExpiresAt =
                this.subscriptionEndDate.atTime(23, 59, 59);
    }

}
