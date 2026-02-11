package com.moneylegal.tenant.repository;

import com.moneylegal.tenant.entity.AccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, String> {
    
    List<AccessRequest> findByTenantIdOrderByCreatedAtDesc(String tenantId);
    
    List<AccessRequest> findByTenantIdAndStatusOrderByCreatedAtDesc(
        String tenantId, 
        AccessRequest.RequestStatus status
    );
    
    Optional<AccessRequest> findByTenantIdAndUserIdAndStatus(
        String tenantId, 
        String userId, 
        AccessRequest.RequestStatus status
    );
    
    List<AccessRequest> findByUserIdOrderByCreatedAtDesc(String userId);
}
