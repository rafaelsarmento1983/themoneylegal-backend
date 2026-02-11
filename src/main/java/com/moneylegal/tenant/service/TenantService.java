package com.moneylegal.tenant.service;

import com.moneylegal.tenant.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TenantService {
    
    TenantResponseDTO createTenant(CreateTenantDTO request, String userId);
    
    TenantResponseDTO getTenant(String tenantId, String userId);
    
    List<TenantResponseDTO> getUserTenants(String userId);
    
    TenantResponseDTO updateTenant(String tenantId, UpdateTenantDTO request, String userId);
    
    void deleteTenant(String tenantId, String userId);
    
    TenantSettingsDTO getSettings(String tenantId, String userId);
    
    TenantSettingsDTO updateSettings(String tenantId, TenantSettingsDTO request, String userId);
    
    /**
     * Buscar todos os tenants públicos (paginado)
     */
    Page<TenantResponseDTO> getAllPublicTenants(int page, int size, String search);
    
    /**
     * Buscar tenants do usuário com filtro
     */
    List<TenantResponseDTO> searchUserTenants(String userId, String search);
}
