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
public class InvitationDTO {

    private String id;
    private String tenantId;
    private String tenantName;
    private String email;
    private String code;
    private String role;
    private String invitedBy;
    private String invitedByName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean isExpired;
}
