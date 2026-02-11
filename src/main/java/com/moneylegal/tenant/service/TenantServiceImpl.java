package com.moneylegal.tenant.service;

import com.moneylegal.exception.*;
import com.moneylegal.tenant.dto.*;
import com.moneylegal.tenant.entity.*;
import com.moneylegal.tenant.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMemberRepository memberRepository;

    @Override
    @Transactional
    public TenantResponseDTO createTenant(CreateTenantDTO request, String userId) {
        String slug = generateSlug(request.getName());

        Tenant.TenantType type = Tenant.TenantType.valueOf(request.getType());
        Tenant.TenantPlan plan = (request.getPlan() != null && !request.getPlan().isBlank())
                ? Tenant.TenantPlan.valueOf(request.getPlan())
                : Tenant.TenantPlan.FREE;

        Tenant tenant = Tenant.builder()
                .name(request.getName())
                .slug(slug)
                .type(type)
                .plan(plan)
                .ownerId(userId)
                .logoUrl(request.getLogoUrl())
                .primaryColor(request.getPrimaryColor())
                .isActive(true)
                // ✅ evita NPE e garante estado consistente mesmo se startSubscription não setar status
                .subscriptionStatus(Tenant.SubscriptionStatus.ACTIVE)
                .build();

        // ⚠️ seu startSubscription atualmente só seta datas e plano (não seta status/expireAt)
        tenant.startSubscription(plan, 365);

        // ✅ garante status e expiresAt coerentes
        if (tenant.getSubscriptionStatus() == null) {
            tenant.setSubscriptionStatus(Tenant.SubscriptionStatus.ACTIVE);
        }
        if (tenant.getSubscriptionExpiresAt() == null) {
            // se você usa expiresAt como "fim", alinhe com subscriptionEndDate
            tenant.setSubscriptionExpiresAt(tenant.getSubscriptionEndDate().atStartOfDay());
        }

        tenant = tenantRepository.save(tenant);

        TenantMember membership = TenantMember.builder()
                .tenantId(tenant.getId())
                .userId(userId)
                .role(TenantMember.MemberRole.OWNER)
                .isActive(true)
                .build();
        memberRepository.save(membership);

        return buildResponse(tenant, TenantMember.MemberRole.OWNER);
    }

    @Override
    public TenantResponseDTO getTenant(String tenantId, String userId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        TenantMember membership = memberRepository.findByTenantIdAndUserId(tenantId, userId)
                .orElseThrow(() -> new UnauthorizedException("Not a member of this tenant"));

        return buildResponse(tenant, membership.getRole());
    }

    @Override
    public List<TenantResponseDTO> getUserTenants(String userId) {
        List<TenantMember> memberships = memberRepository.findByUserIdAndIsActiveTrue(userId);

        return memberships.stream()
                .map(m -> {
                    Tenant tenant = tenantRepository.findById(m.getTenantId()).orElse(null);
                    return tenant != null ? buildResponse(tenant, m.getRole()) : null;
                })
                .filter(t -> t != null)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TenantResponseDTO updateTenant(String tenantId, UpdateTenantDTO request, String userId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        TenantMember membership = memberRepository.findByTenantIdAndUserId(tenantId, userId)
                .orElseThrow(() -> new UnauthorizedException("Not authorized"));

        if (!membership.isAdmin()) {
            throw new UnauthorizedException("Only admins can update tenant");
        }

        if (request.getName() != null) tenant.setName(request.getName());
        if (request.getLogoUrl() != null) tenant.setLogoUrl(request.getLogoUrl());
        if (request.getPrimaryColor() != null) tenant.setPrimaryColor(request.getPrimaryColor());

        tenant = tenantRepository.save(tenant);
        return buildResponse(tenant, membership.getRole());
    }

    @Override
    @Transactional
    public void deleteTenant(String tenantId, String userId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        TenantMember membership = memberRepository.findByTenantIdAndUserId(tenantId, userId)
                .orElseThrow(() -> new UnauthorizedException("Not authorized"));

        if (!membership.isOwner()) {
            throw new UnauthorizedException("Only owner can delete tenant");
        }

        tenantRepository.delete(tenant);
    }

    @Override
    public TenantSettingsDTO getSettings(String tenantId, String userId) {
        // TODO: Implement settings retrieval
        return TenantSettingsDTO.builder()
                .tenantId(tenantId)
                .emailNotificationsEnabled(true)
                .pushNotificationsEnabled(true)
                .build();
    }

    @Override
    @Transactional
    public TenantSettingsDTO updateSettings(String tenantId, TenantSettingsDTO request, String userId) {
        // TODO: Implement settings update
        return request;
    }

    @Override
    public Page<TenantResponseDTO> getAllPublicTenants(int page, int size, String search) {
        log.info("Getting public tenants - page: {}, size: {}, search: {}", page, size, search);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Tenant> tenants;
        if (search != null && !search.trim().isEmpty()) {
            tenants = tenantRepository.findAllActiveByNameOrSlug(search.trim(), pageable);
        } else {
            tenants = tenantRepository.findAllByIsActiveTrue(pageable);
        }

        return tenants.map(this::buildPublicResponse);
    }

    @Override
    public List<TenantResponseDTO> searchUserTenants(String userId, String search) {
        log.info("Searching user tenants - userId: {}, search: {}", userId, search);

        List<Tenant> tenants;
        if (search != null && !search.trim().isEmpty()) {
            tenants = tenantRepository.findByUserIdAndNameOrSlug(userId, search.trim());
        } else {
            tenants = tenantRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }

        return tenants.stream()
                .map(tenant -> {
                    TenantMember membership = memberRepository.findByTenantIdAndUserId(tenant.getId(), userId)
                            .orElse(null);
                    TenantMember.MemberRole role = membership != null ? membership.getRole() : TenantMember.MemberRole.MEMBER;
                    return buildResponse(tenant, role);
                })
                .collect(Collectors.toList());
    }

    private String generateSlug(String name) {
        String baseSlug = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .trim();

        String slug = baseSlug;
        int counter = 1;
        while (tenantRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }

    private TenantResponseDTO buildResponse(Tenant tenant, TenantMember.MemberRole role) {
        long memberCount = memberRepository.countByTenantIdAndIsActiveTrue(tenant.getId());

        // ✅ null-safe para não explodir caso existam linhas antigas com null no banco
        String subscriptionStatus = (tenant.getSubscriptionStatus() != null)
                ? tenant.getSubscriptionStatus().name()
                : "UNKNOWN";

        return TenantResponseDTO.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .slug(tenant.getSlug())
                .type(tenant.getType().name())
                .plan(tenant.getPlan().name())
                .ownerId(tenant.getOwnerId())
                .subscriptionStatus(subscriptionStatus)
                .subscriptionExpiresAt(tenant.getSubscriptionExpiresAt())
                .maxMembers(tenant.getMaxMembers())
                .maxAccounts(tenant.getMaxAccounts())
                .maxBudgets(tenant.getMaxBudgets())
                .logoUrl(tenant.getLogoUrl())
                .primaryColor(tenant.getPrimaryColor())
                .isActive(tenant.getIsActive())
                .createdAt(tenant.getCreatedAt())
                .currentMemberCount((int) memberCount)
                .userRole(role.name())
                .build();
    }

    private TenantResponseDTO buildPublicResponse(Tenant tenant) {
        long memberCount = memberRepository.countByTenantIdAndIsActiveTrue(tenant.getId());

        return TenantResponseDTO.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .slug(tenant.getSlug())
                .type(tenant.getType().name())
                .plan(tenant.getPlan().name())
                .logoUrl(tenant.getLogoUrl())
                .primaryColor(tenant.getPrimaryColor())
                .createdAt(tenant.getCreatedAt())
                .currentMemberCount((int) memberCount)
                .build();
    }
}
