package com.bloodbank.bdms.repository;

import com.bloodbank.bdms.entity.DonorProfile;
import com.bloodbank.bdms.entity.enums.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonorProfileRepository extends JpaRepository<DonorProfile, Long> {
  List<DonorProfile> findByBloodGroupAndAvailableTrue(BloodGroup bloodGroup);
}
