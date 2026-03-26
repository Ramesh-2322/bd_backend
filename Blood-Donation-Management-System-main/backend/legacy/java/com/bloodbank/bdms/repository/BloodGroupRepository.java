package com.bloodbank.bdms.repository;

import com.bloodbank.bdms.entity.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BloodGroupRepository extends JpaRepository<BloodGroup, Long> {
    Optional<BloodGroup> findByName(String name);
}
