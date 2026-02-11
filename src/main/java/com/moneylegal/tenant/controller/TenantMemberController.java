package com.moneylegal.tenant.controller;

import com.moneylegal.tenant.dto.*;
import com.moneylegal.tenant.service.TenantMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TenantMemberController - Gestão de Membros do Tenant
 */
@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/members")
@RequiredArgsConstructor
@Slf4j
public class TenantMemberController {

    private final TenantMemberService memberService;

    @GetMapping
    public ResponseEntity<List<TenantMemberDTO>> getMembers(
        @PathVariable String tenantId,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        List<TenantMemberDTO> members = memberService.getMembers(tenantId, userId);
        return ResponseEntity.ok(members);
    }

    @PostMapping("/invite")
    public ResponseEntity<InvitationDTO> inviteMember(
        @PathVariable String tenantId,
        @Valid @RequestBody InviteMemberDTO request,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        InvitationDTO invitation = memberService.inviteMember(tenantId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(invitation);
    }

    @PostMapping("/accept")
    public ResponseEntity<TenantMemberDTO> acceptInvitation(
        @PathVariable String tenantId,
        @Valid @RequestBody AcceptInvitationDTO request,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        TenantMemberDTO member = memberService.acceptInvitation(request.getCode(), userId);
        return ResponseEntity.ok(member);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(
        @PathVariable String tenantId,
        @PathVariable String memberId,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        memberService.removeMember(tenantId, memberId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{memberId}/role")
    public ResponseEntity<TenantMemberDTO> updateRole(
        @PathVariable String tenantId,
        @PathVariable String memberId,
        @RequestParam String role,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        TenantMemberDTO member = memberService.updateRole(tenantId, memberId, role, userId);
        return ResponseEntity.ok(member);
    }
}
