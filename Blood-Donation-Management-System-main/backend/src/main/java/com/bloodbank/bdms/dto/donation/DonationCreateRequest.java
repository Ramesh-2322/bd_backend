package com.bloodbank.bdms.dto.donation;

import com.bloodbank.bdms.entity.enums.DonationStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DonationCreateRequest {
  @NotNull
  private Long donorId;

  @NotNull
  private LocalDate donationDate;

  @Min(100)
  private int quantityMl;

  private String center;

  private DonationStatus status = DonationStatus.COMPLETED;

  private String notes;
}
