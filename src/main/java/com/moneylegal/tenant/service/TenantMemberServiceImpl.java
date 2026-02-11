package com.moneylegal.tenant.service;

import com.moneylegal.auth.entity.User;
import com.moneylegal.auth.repository.UserRepository;
import com.moneylegal.exception.*;
import com.moneylegal.tenant.dto.*;
import com.moneylegal.tenant.entity.*;
import com.moneylegal.tenant.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantMemberServiceImpl implements TenantMemberService {

    private final TenantMemberRepository memberRepository;
    private final InvitationRepository invitationRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;

    @Override
    public List<TenantMemberDTO> getMembers(String tenantId, String userId) {
        verifyMembership(tenantId, userId);
        
        List<TenantMember> members = memberRepository.findByTenantIdAndIsActiveTrue(tenantId);
        
        return members.stream()
            .map(this::buildMemberDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InvitationDTO inviteMember(String tenantId, InviteMemberDTO request, String userId) {
        TenantMember requester = memberRepository.findByTenantIdAndUserId(tenantId, userId)
            .orElseThrow(() -> new UnauthorizedException("Not authorized"));
        
        if (!requester.canManageMembers()) {
            throw new UnauthorizedException("Only admins can invite members");
        }
        
        if (invitationRepository.existsPendingInvitation(tenantId, request.getEmail())) {
            throw new BadRequestException("Invitation already sent to this email");
        }
        
        String code = generateInvitationCode();
        
        Invitation invitation = Invitation.create(
            tenantId,
            request.getEmail(),
            code,
            TenantMember.MemberRole.valueOf(request.getRole()),
            userId,
            7
        );
        
        invitation = invitationRepository.save(invitation);
        
        return buildInvitationDTO(invitation);
    }

    @Override
    @Transactional
    public TenantMemberDTO acceptInvitation(String code, String userId) {
        Invitation invitation = invitationRepository.findValidByCode(code, LocalDateTime.now())
            .orElseThrow(() -> new BadRequestException("Invalid or expired invitation"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!user.getEmail().equals(invitation.getEmail())) {
            throw new BadRequestException("This invitation is for a different email");
        }
        
        TenantMember member = TenantMember.builder()
            .tenantId(invitation.getTenantId())
            .userId(userId)
            .role(invitation.getRole())
            .invitedBy(invitation.getInvitedBy())
            .isActive(true)
            .build();
        
        member = memberRepository.save(member);
        
        invitation.accept(userId);
        invitationRepository.save(invitation);
        
        return buildMemberDTO(member);
    }

    @Override
    @Transactional
    public void removeMember(String tenantId, String memberId, String userId) {
        TenantMember requester = memberRepository.findByTenantIdAndUserId(tenantId, userId)
            .orElseThrow(() -> new UnauthorizedException("Not authorized"));
        
        if (!requester.canManageMembers()) {
            throw new UnauthorizedException("Only admins can remove members");
        }
        
        TenantMember member = memberRepository.findById(memberId)
            .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        
        if (member.isOwner()) {
            throw new BadRequestException("Cannot remove owner");
        }
        
        member.deactivate();
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public TenantMemberDTO updateRole(String tenantId, String memberId, String role, String userId) {
        TenantMember requester = memberRepository.findByTenantIdAndUserId(tenantId, userId)
            .orElseThrow(() -> new UnauthorizedException("Not authorized"));
        
        if (!requester.isOwner()) {
            throw new UnauthorizedException("Only owner can change roles");
        }
        
        TenantMember member = memberRepository.findById(memberId)
            .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        
        if (member.isOwner()) {
            throw new BadRequestException("Cannot change owner role");
        }
        
        TenantMember.MemberRole newRole = TenantMember.MemberRole.valueOf(role);
        member.setRole(newRole);
        member = memberRepository.save(member);
        
        return buildMemberDTO(member);
    }

    private void verifyMembership(String tenantId, String userId) {
        if (!memberRepository.existsByTenantIdAndUserIdAndIsActiveTrue(tenantId, userId)) {
            throw new UnauthorizedException("Not a member of this tenant");
        }
    }

    private String generateInvitationCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    private TenantMemberDTO buildMemberDTO(TenantMember member) {
        User user = userRepository.findById(member.getUserId()).orElse(null);
        User inviter = member.getInvitedBy() != null ? userRepository.findById(member.getInvitedBy()).orElse(null) : null;
        
        return TenantMemberDTO.builder()
            .id(member.getId())
            .tenantId(member.getTenantId())
            .userId(member.getUserId())
            .userName(user != null ? user.getName() : "Unknown")
            .userEmail(user != null ? user.getEmail() : "Unknown")
            .userAvatarUrl(user != null ? user.getAvatarUrl() : null)
            .role(member.getRole().name())
            .invitedBy(member.getInvitedBy())
            .invitedByName(inviter != null ? inviter.getName() : null)
            .joinedAt(member.getJoinedAt())
            .isActive(member.getIsActive())
            .build();
    }

    private InvitationDTO buildInvitationDTO(Invitation invitation) {
        Tenant tenant = tenantRepository.findById(invitation.getTenantId()).orElse(null);
        User inviter = userRepository.findById(invitation.getInvitedBy()).orElse(null);
        
        return InvitationDTO.builder()
            .id(invitation.getId())
            .tenantId(invitation.getTenantId())
            .tenantName(tenant != null ? tenant.getName() : "Unknown")
            .email(invitation.getEmail())
            .code(invitation.getCode())
            .role(invitation.getRole().name())
            .invitedBy(invitation.getInvitedBy())
            .invitedByName(inviter != null ? inviter.getName() : null)
            .status(invitation.getStatus().name())
            .createdAt(invitation.getCreatedAt())
            .expiresAt(invitation.getExpiresAt())
            .isExpired(invitation.isExpired())
            .build();
    }
}
