package com.moneylegal.tenant.service;

import com.moneylegal.exception.*;
import com.moneylegal.tenant.dto.*;
import com.moneylegal.tenant.entity.*;
import com.moneylegal.tenant.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccessRequestServiceImpl implements AccessRequestService {

    private final AccessRequestRepository accessRequestRepository;
    private final TenantRepository tenantRepository;
    private final TenantMemberRepository memberRepository;

    @Override
    @Transactional
    public AccessRequestResponseDTO createAccessRequest(CreateAccessRequestDTO request, String userId) {
        log.info("Creating access request for tenant: {} by user: {}", request.getTenantId(), userId);
        
        Tenant tenant = tenantRepository.findById(request.getTenantId())
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        
        boolean isMember = memberRepository.findByTenantIdAndUserId(request.getTenantId(), userId)
            .isPresent();
        
        if (isMember) {
            throw new BadRequestException("You are already a member of this tenant");
        }
        
        accessRequestRepository.findByTenantIdAndUserIdAndStatus(
            request.getTenantId(), 
            userId, 
            AccessRequest.RequestStatus.PENDING
        ).ifPresent(existing -> {
            throw new BadRequestException("You already have a pending request for this tenant");
        });
        
        AccessRequest accessRequest = AccessRequest.builder()
            .tenantId(request.getTenantId())
            .userId(userId)
            .message(request.getMessage())
            .status(AccessRequest.RequestStatus.PENDING)
            .build();
        
        accessRequest = accessRequestRepository.save(accessRequest);
        
        log.info("Access request created with ID: {}", accessRequest.getId());
        
        return buildResponse(accessRequest, tenant);
    }

    @Override
    public List<AccessRequestResponseDTO> getTenantAccessRequests(String tenantId, String userId) {
        log.info("Getting access requests for tenant: {} by user: {}", tenantId, userId);
        
        TenantMember membership = memberRepository.findByTenantIdAndUserId(tenantId, userId)
            .orElseThrow(() -> new UnauthorizedException("Not authorized to view access requests"));
        
        if (!membership.isAdmin()) {
            throw new UnauthorizedException("Only admins can view access requests");
        }
        
        Tenant tenant = tenantRepository.findById(tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));
        
        List<AccessRequest> requests = accessRequestRepository
            .findByTenantIdAndStatusOrderByCreatedAtDesc(tenantId, AccessRequest.RequestStatus.PENDING);
        
        return requests.stream()
            .map(request -> buildResponse(request, tenant))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approveAccessRequest(String requestId, String userId) {
        log.info("Approving access request: {} by user: {}", requestId, userId);
        
        AccessRequest request = accessRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Access request not found"));
        
        TenantMember membership = memberRepository.findByTenantIdAndUserId(request.getTenantId(), userId)
            .orElseThrow(() -> new UnauthorizedException("Not authorized to approve access requests"));
        
        if (!membership.isAdmin()) {
            throw new UnauthorizedException("Only admins can approve access requests");
        }
        
        if (request.getStatus() != AccessRequest.RequestStatus.PENDING) {
            throw new BadRequestException("Access request is not pending");
        }
        
        request.setStatus(AccessRequest.RequestStatus.APPROVED);
        accessRequestRepository.save(request);
        
        TenantMember newMember = TenantMember.builder()
            .tenantId(request.getTenantId())
            .userId(request.getUserId())
            .role(TenantMember.MemberRole.MEMBER)
            .isActive(true)
            .build();
        
        memberRepository.save(newMember);
        
        log.info("Access request approved and user added as member");
    }

    @Override
    @Transactional
    public void rejectAccessRequest(String requestId, String userId) {
        log.info("Rejecting access request: {} by user: {}", requestId, userId);
        
        AccessRequest request = accessRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Access request not found"));
        
        TenantMember membership = memberRepository.findByTenantIdAndUserId(request.getTenantId(), userId)
            .orElseThrow(() -> new UnauthorizedException("Not authorized to reject access requests"));
        
        if (!membership.isAdmin()) {
            throw new UnauthorizedException("Only admins can reject access requests");
        }
        
        if (request.getStatus() != AccessRequest.RequestStatus.PENDING) {
            throw new BadRequestException("Access request is not pending");
        }
        
        request.setStatus(AccessRequest.RequestStatus.REJECTED);
        accessRequestRepository.save(request);
        
        log.info("Access request rejected");
    }

    private AccessRequestResponseDTO buildResponse(AccessRequest request, Tenant tenant) {
        return AccessRequestResponseDTO.builder()
            .id(request.getId())
            .tenantId(request.getTenantId())
            .tenantName(tenant != null ? tenant.getName() : null)
            .tenantSlug(tenant != null ? tenant.getSlug() : null)
            .userId(request.getUserId())
            .message(request.getMessage())
            .status(request.getStatus().name())
            .createdAt(request.getCreatedAt())
            .updatedAt(request.getUpdatedAt())
            .build();
    }
}
