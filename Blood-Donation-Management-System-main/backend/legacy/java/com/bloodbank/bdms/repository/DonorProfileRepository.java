package com.bloodbank.bdms.repository;

import com.bloodbank.bdms.entity.BloodGroup;
import com.bloodbank.bdms.entity.DonorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DonorProfileRepository extends JpaRepository<DonorProfile, Long> {
    Optional<DonorProfile> findByUserId(Long userId);
    List<DonorProfile> findByBloodGroup(BloodGroup bloodGroup);
    List<DonorProfile> findByBloodGroupAndReadyToDonateTrue(BloodGroup bloodGroup);
    List<DonorProfile> findByReadyToDonateTrue();
    long countByBloodGroup(BloodGroup bloodGroup);
}
