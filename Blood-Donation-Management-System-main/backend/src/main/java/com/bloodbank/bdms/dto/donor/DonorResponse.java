package com.bloodbank.bdms.dto.donor;

import com.bloodbank.bdms.entity.enums.BloodGroup;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Builder
public class DonorResponse {
  private Long id;
  private Long userId;
  private String name;
  private String email;
  private String phone;
  private BloodGroup bloodGroup;
  private String gender;
  private LocalDate dateOfBirth;
  private String address;
  private String city;
  private LocalDate lastDonationDate;
  private LocalDate nextEligibleDate;
  private boolean available;
  private int totalDonations;
  private Instant createdAt;
}
