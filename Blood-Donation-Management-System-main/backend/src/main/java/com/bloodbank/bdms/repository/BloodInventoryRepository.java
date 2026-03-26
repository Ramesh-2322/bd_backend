package com.bloodbank.bdms.repository;

import com.bloodbank.bdms.entity.BloodInventory;
import com.bloodbank.bdms.entity.enums.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BloodInventoryRepository extends JpaRepository<BloodInventory, Long> {
  Optional<BloodInventory> findByBloodGroup(BloodGroup bloodGroup);
}
