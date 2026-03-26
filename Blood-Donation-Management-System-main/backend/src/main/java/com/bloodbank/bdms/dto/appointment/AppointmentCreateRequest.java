package com.bloodbank.bdms.dto.appointment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentCreateRequest {
  @NotNull
  private Long donorId;

  @NotNull
  private LocalDateTime scheduledAt;

  @NotNull
  private String location;

  private String notes;
}
