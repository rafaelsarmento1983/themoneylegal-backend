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
public class AccessRequestResponseDTO {
    
    private String id;
    private String tenantId;
    private String tenantName;
    private String tenantSlug;
    private String userId;
    private String message;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
