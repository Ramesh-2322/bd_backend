package com.bloodbank.bdms.dto.request;

import com.bloodbank.bdms.entity.enums.BloodGroup;
import com.bloodbank.bdms.entity.enums.RequestPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BloodRequestCreateRequest {
  @NotBlank
  private String requesterName;

  @NotBlank
  private String requesterContact;

  @NotNull
  private BloodGroup bloodGroup;

  private int unitsNeeded;

  @NotNull
  private RequestPriority priority;

  private String hospital;

  private LocalDate neededBy;
}
