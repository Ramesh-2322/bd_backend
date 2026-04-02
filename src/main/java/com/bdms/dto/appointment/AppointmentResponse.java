package com.bdms.dto.appointment;

import com.bdms.entity.AppointmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {

    private Long id;
    private Long donorId;
    private String donorName;
    private Long bloodRequestId;
    private String patientName;
    private LocalDateTime appointmentDate;
    private String hospitalName;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
}
