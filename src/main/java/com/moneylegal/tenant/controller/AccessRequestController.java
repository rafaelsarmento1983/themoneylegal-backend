package com.moneylegal.tenant.controller;

import com.moneylegal.tenant.dto.AccessRequestResponseDTO;
import com.moneylegal.tenant.dto.CreateAccessRequestDTO;
import com.moneylegal.tenant.service.AccessRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Slf4j
public class AccessRequestController {

    private final AccessRequestService accessRequestService;

    /**
     * POST /api/v1/tenants/access-requests
     * Criar solicitação de acesso a um tenant
     */
    @PostMapping("/access-requests")
    public ResponseEntity<AccessRequestResponseDTO> createAccessRequest(
        @Valid @RequestBody CreateAccessRequestDTO request,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        log.info("POST /api/v1/tenants/access-requests - userId: {}, tenantId: {}", 
            userId, request.getTenantId());
        
        AccessRequestResponseDTO response = accessRequestService.createAccessRequest(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/tenants/{id}/access-requests
     * Listar solicitações de acesso de um tenant (apenas admins)
     */
    @GetMapping("/{id}/access-requests")
    public ResponseEntity<List<AccessRequestResponseDTO>> getTenantAccessRequests(
        @PathVariable String id,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        log.info("GET /api/v1/tenants/{}/access-requests - userId: {}", id, userId);
        
        List<AccessRequestResponseDTO> requests = accessRequestService.getTenantAccessRequests(id, userId);
        return ResponseEntity.ok(requests);
    }

    /**
     * POST /api/v1/tenants/access-requests/{id}/approve
     * Aprovar solicitação de acesso (apenas admins)
     */
    @PostMapping("/access-requests/{id}/approve")
    public ResponseEntity<Void> approveAccessRequest(
        @PathVariable String id,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        log.info("POST /api/v1/tenants/access-requests/{}/approve - userId: {}", id, userId);
        
        accessRequestService.approveAccessRequest(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/v1/tenants/access-requests/{id}/reject
     * Rejeitar solicitação de acesso (apenas admins)
     */
    @PostMapping("/access-requests/{id}/reject")
    public ResponseEntity<Void> rejectAccessRequest(
        @PathVariable String id,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        log.info("POST /api/v1/tenants/access-requests/{}/reject - userId: {}", id, userId);
        
        accessRequestService.rejectAccessRequest(id, userId);
        return ResponseEntity.noContent().build();
    }
}
