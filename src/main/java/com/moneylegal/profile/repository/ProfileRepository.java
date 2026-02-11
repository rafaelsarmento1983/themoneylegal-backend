package com.moneylegal.profile.repository;

import com.moneylegal.profile.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {
    
    Optional<Profile> findByUserId(String userId);
    
    boolean existsBySlug(String slug);
    
    boolean existsByUserId(String userId);
}
