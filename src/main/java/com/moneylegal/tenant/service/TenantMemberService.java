package com.moneylegal.tenant.service;

import com.moneylegal.tenant.dto.*;
import java.util.List;

public interface TenantMemberService {
    List<TenantMemberDTO> getMembers(String tenantId, String userId);
    InvitationDTO inviteMember(String tenantId, InviteMemberDTO request, String userId);
    TenantMemberDTO acceptInvitation(String code, String userId);
    void removeMember(String tenantId, String memberId, String userId);
    TenantMemberDTO updateRole(String tenantId, String memberId, String role, String userId);
}
