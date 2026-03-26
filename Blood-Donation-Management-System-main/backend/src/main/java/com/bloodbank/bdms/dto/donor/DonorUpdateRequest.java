package com.bloodbank.bdms.dto.donor;

import com.bloodbank.bdms.entity.enums.BloodGroup;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DonorUpdateRequest {
  private String name;
  private String phone;
  private BloodGroup bloodGroup;
  private String gender;
  private LocalDate dateOfBirth;
  private String address;
  private String city;
  private LocalDate lastDonationDate;
  private LocalDate nextEligibleDate;
  private Boolean available;
  private Integer totalDonations;
}
