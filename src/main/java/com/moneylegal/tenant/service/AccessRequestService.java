package com.moneylegal.tenant.service;

import com.moneylegal.tenant.dto.AccessRequestResponseDTO;
import com.moneylegal.tenant.dto.CreateAccessRequestDTO;

import java.util.List;

public interface AccessRequestService {
    
    AccessRequestResponseDTO createAccessRequest(CreateAccessRequestDTO request, String userId);
    
    List<AccessRequestResponseDTO> getTenantAccessRequests(String tenantId, String userId);
    
    void approveAccessRequest(String requestId, String userId);
    
    void rejectAccessRequest(String requestId, String userId);
}
