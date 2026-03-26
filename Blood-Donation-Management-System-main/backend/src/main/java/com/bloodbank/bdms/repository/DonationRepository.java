package com.bloodbank.bdms.repository;

import com.bloodbank.bdms.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {
  List<Donation> findByDonorId(Long donorId);
}
