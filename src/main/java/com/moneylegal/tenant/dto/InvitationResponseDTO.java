package com.moneylegal.tenant.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponseDTO {

    private String id;
    private String tenantId;
    private String tenantName;
    private String email;
    private String code;
    private String role;
    private String invitedByName;
    private String status; // PENDING, ACCEPTED, REJECTED, CANCELLED, EXPIRED
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
