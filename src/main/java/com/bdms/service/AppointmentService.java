package com.bdms.service;

import com.bdms.dto.appointment.AppointmentCreateRequest;
import com.bdms.dto.appointment.AppointmentResponse;
import com.bdms.dto.appointment.AppointmentStatusUpdateRequest;
import com.bdms.dto.appointment.UserAppointmentResponse;
import com.bdms.entity.AppointmentStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AppointmentService {

    AppointmentResponse bookAppointment(AppointmentCreateRequest request);

    Page<AppointmentResponse> getMyAppointments(int page, int size, String sortBy, String sortDir,
                                                AppointmentStatus status, String hospitalName);

    Page<AppointmentResponse> getAllAppointments(int page, int size, String sortBy, String sortDir,
                                                 AppointmentStatus status, String hospitalName);

    List<UserAppointmentResponse> getAppointmentsByUserId(Long userId);

    AppointmentResponse updateStatus(Long appointmentId, AppointmentStatusUpdateRequest request);
}
