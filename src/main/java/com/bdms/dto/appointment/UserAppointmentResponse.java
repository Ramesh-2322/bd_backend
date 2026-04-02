package com.bdms.dto.appointment;

import com.bdms.entity.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAppointmentResponse {

    private Long id;
    private Long userId;
    private String hospitalName;
    private LocalDateTime date;
    private AppointmentStatus status;
}
