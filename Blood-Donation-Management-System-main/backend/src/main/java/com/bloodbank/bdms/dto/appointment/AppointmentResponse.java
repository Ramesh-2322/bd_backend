package com.bloodbank.bdms.dto.appointment;

import com.bloodbank.bdms.entity.enums.AppointmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AppointmentResponse {
  private Long id;
  private Long donorId;
  private String donorName;
  private LocalDateTime scheduledAt;
  private String location;
  private AppointmentStatus status;
  private String notes;
}
