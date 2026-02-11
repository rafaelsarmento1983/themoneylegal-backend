package com.moneylegal.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponseDTO {

    private String id;
    private String name;
    private String slug;
    private String type;
    private String plan;
    private String ownerId;
    private String subscriptionStatus;
    private LocalDateTime subscriptionExpiresAt;
    private Integer maxMembers;
    private Integer maxAccounts;
    private Integer maxBudgets;
    private String logoUrl;
    private String primaryColor;
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    // Informações extras
    private Integer currentMemberCount;
    private Integer currentAccountCount;
    private Integer currentBudgetCount;
    private String userRole; // Role do usuário neste tenant
}
