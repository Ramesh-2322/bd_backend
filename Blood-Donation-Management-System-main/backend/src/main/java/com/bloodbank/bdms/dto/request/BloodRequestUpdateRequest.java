package com.bloodbank.bdms.dto.request;

import com.bloodbank.bdms.entity.enums.RequestPriority;
import com.bloodbank.bdms.entity.enums.RequestStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BloodRequestUpdateRequest {
  private RequestStatus status;
  private Integer matchedDonors;
  private Integer unitsNeeded;
  private RequestPriority priority;
  private String hospital;
  private LocalDate neededBy;
}
