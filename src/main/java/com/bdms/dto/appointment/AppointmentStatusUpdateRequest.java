package com.bdms.dto.appointment;

import com.bdms.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentStatusUpdateRequest {

    @NotNull(message = "Appointment status is required")
    private AppointmentStatus status;
}
