package com.moneylegal.tenant.controller;

import com.moneylegal.tenant.dto.*;
import com.moneylegal.tenant.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TenantController - Endpoints de Gestão de Tenants
 * 
 * Endpoints:
 * POST   /api/v1/tenants              - Criar tenant
 * GET    /api/v1/tenants              - Listar tenants do usuário
 * GET    /api/v1/tenants/{id}         - Buscar tenant por ID
 * PUT    /api/v1/tenants/{id}         - Atualizar tenant
 * DELETE /api/v1/tenants/{id}         - Deletar tenant
 * GET    /api/v1/tenants/{id}/settings - Buscar configurações
 * PUT    /api/v1/tenants/{id}/settings - Atualizar configurações
 */
@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
@Slf4j
public class TenantController {

    private final TenantService tenantService;

    /**
     * POST /api/v1/tenants
     * Criar novo tenant
     */
    @PostMapping
    public ResponseEntity<TenantResponseDTO> createTenant(
        @Valid @RequestBody CreateTenantDTO request,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        log.info("POST /api/v1/tenants - userId: {}", userId);
        
        TenantResponseDTO response = tenantService.createTenant(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/tenants
     * Listar todos os tenants do usuário
     */
    @GetMapping
    public ResponseEntity<List<TenantResponseDTO>> getUserTenants(Authentication authentication) {
        String userId = authentication.getName();
        log.info("GET /api/v1/tenants - userId: {}", userId);
        
        List<TenantResponseDTO> tenants = tenantService.getUserTenants(userId);
        return ResponseEntity.ok(tenants);
    }

    /**
     * GET /api/v1/tenants/{id}
     * Buscar tenant por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TenantResponseDTO> getTenant(
        @PathVariable String id,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        log.info("GET /api/v1/tenants/{} - userId: {}", id, userId);
        
        TenantResponseDTO tenant = tenantService.getTenant(id, userId);
        return ResponseEntity.ok(tenant);
    }

    /**
     * PUT /api/v1/tenants/{id}
     * Atualizar tenant
     */
    @PutMapping("/{id}")
    public ResponseEntity<TenantResponseDTO> updateTenant(
        @PathVariable String id,
        @Valid @RequestBody UpdateTenantDTO request,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        log.info("PUT /api/v1/tenants/{} - userId: {}", id, userId);
        
        TenantResponseDTO tenant = tenantService.updateTenant(id, request, userId);
        return ResponseEntity.ok(tenant);
    }

    /**
     * DELETE /api/v1/tenants/{id}
     * Deletar tenant (somente OWNER)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(
        @PathVariable String id,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        log.info("DELETE /api/v1/tenants/{} - userId: {}", id, userId);
        
        tenantService.deleteTenant(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/tenants/{id}/settings
     * Buscar configurações do tenant
     */
    @GetMapping("/{id}/settings")
    public ResponseEntity<TenantSettingsDTO> getSettings(
        @PathVariable String id,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        log.info("GET /api/v1/tenants/{}/settings - userId: {}", id, userId);
        
        TenantSettingsDTO settings = tenantService.getSettings(id, userId);
        return ResponseEntity.ok(settings);
    }

    /**
     * PUT /api/v1/tenants/{id}/settings
     * Atualizar configurações do tenant
     */
    @PutMapping("/{id}/settings")
    public ResponseEntity<TenantSettingsDTO> updateSettings(
        @PathVariable String id,
        @Valid @RequestBody TenantSettingsDTO request,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        log.info("PUT /api/v1/tenants/{}/settings - userId: {}", id, userId);
        
        TenantSettingsDTO settings = tenantService.updateSettings(id, request, userId);
        return ResponseEntity.ok(settings);
    }

    /**
     * GET /api/v1/tenants/public
     * Buscar todos os tenants públicos (paginado)
     * Endpoint público - não requer autenticação
     */
    @GetMapping("/public")
    public ResponseEntity<Page<TenantResponseDTO>> getAllPublicTenants(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search
    ) {
        log.info("GET /api/v1/tenants/public - page: {}, size: {}, search: {}", page, size, search);
        
        Page<TenantResponseDTO> tenants = tenantService.getAllPublicTenants(page, size, search);
        return ResponseEntity.ok(tenants);
    }

    /**
     * GET /api/v1/tenants/search
     * Buscar tenants do usuário com filtro
     */
    @GetMapping("/search")
    public ResponseEntity<List<TenantResponseDTO>> searchUserTenants(
        @RequestParam String q,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        log.info("GET /api/v1/tenants/search - userId: {}, query: {}", userId, q);
        
        List<TenantResponseDTO> tenants = tenantService.searchUserTenants(userId, q);
        return ResponseEntity.ok(tenants);
    }
}
