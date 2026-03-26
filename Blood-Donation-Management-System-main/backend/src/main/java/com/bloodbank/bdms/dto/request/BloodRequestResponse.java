package com.bloodbank.bdms.dto.request;

import com.bloodbank.bdms.entity.enums.BloodGroup;
import com.bloodbank.bdms.entity.enums.RequestPriority;
import com.bloodbank.bdms.entity.enums.RequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Builder
public class BloodRequestResponse {
  private Long id;
  private String requesterName;
  private String requesterContact;
  private BloodGroup bloodGroup;
  private int unitsNeeded;
  private RequestPriority priority;
  private String hospital;
  private RequestStatus status;
  private LocalDate neededBy;
  private int matchedDonors;
  private Instant createdAt;
}
