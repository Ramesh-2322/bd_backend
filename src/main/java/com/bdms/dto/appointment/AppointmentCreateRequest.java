package com.bdms.dto.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentCreateRequest {

    @NotNull(message = "Blood request id is required")
    private Long bloodRequestId;

    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;

    @NotBlank(message = "Hospital name is required")
    @Size(max = 150, message = "Hospital name must be at most 150 characters")
    private String hospitalName;
}
