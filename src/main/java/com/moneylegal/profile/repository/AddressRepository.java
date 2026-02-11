package com.moneylegal.profile.repository;

import com.moneylegal.profile.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {

    Optional<Address> findByProfileId(String profileId);

    boolean existsByProfileId(String profileId);

    void deleteByProfileId(String profileId);
}
