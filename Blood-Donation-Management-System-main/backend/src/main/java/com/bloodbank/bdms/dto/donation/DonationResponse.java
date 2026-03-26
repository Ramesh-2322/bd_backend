package com.bloodbank.bdms.dto.donation;

import com.bloodbank.bdms.entity.enums.DonationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DonationResponse {
  private Long id;
  private Long donorId;
  private String donorName;
  private LocalDate donationDate;
  private int quantityMl;
  private String center;
  private DonationStatus status;
  private String notes;
}
