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
public class TenantMemberDTO {

    private String id;
    private String tenantId;
    private String userId;
    private String userName;
    private String userEmail;
    private String userAvatarUrl;
    private String role;
    private String invitedBy;
    private String invitedByName;
    private LocalDateTime joinedAt;
    private Boolean isActive;
}
