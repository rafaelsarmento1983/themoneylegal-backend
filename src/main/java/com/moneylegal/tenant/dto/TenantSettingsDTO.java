package com.moneylegal.tenant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSettingsDTO {

    private String tenantId;
    
    // Configurações de Notificação
    private Boolean emailNotificationsEnabled;
    private Boolean pushNotificationsEnabled;
    private Boolean whatsappNotificationsEnabled;
    
    // Configurações de Budget
    private Boolean budgetAlertsEnabled;
    private Integer budgetAlertThreshold; // Percentual (ex: 80 = 80%)
    
    // Configurações de Aprovação
    private Boolean approvalWorkflowEnabled;
    private Double approvalThresholdAmount; // Valor mínimo para requerer aprovação
    
    // Configurações de Visibilidade
    private Boolean allowMembersToSeeAllTransactions;
    private Boolean allowMembersToCreateCategories;
    private Boolean allowMembersToInviteOthers;
    
    // Configurações de AI
    private Boolean aiCategorizationEnabled;
    private Boolean aiCoachEnabled;
    private Boolean aiInsightsEnabled;
    
    // Configurações de Gamificação
    private Boolean gamificationEnabled;
    private Boolean leaderboardEnabled;
    private Boolean achievementsEnabled;
}
