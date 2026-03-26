package com.bloodbank.bdms.repository;

import com.bloodbank.bdms.entity.BloodRequest;
import com.bloodbank.bdms.entity.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {
  List<BloodRequest> findByStatus(RequestStatus status);
}
