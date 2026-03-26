package com.bloodbank.bdms.controller;

import com.bloodbank.bdms.dto.appointment.AppointmentCreateRequest;
import com.bloodbank.bdms.dto.appointment.AppointmentResponse;
import com.bloodbank.bdms.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
  private final AppointmentService appointmentService;

  public AppointmentController(AppointmentService appointmentService) {
    this.appointmentService = appointmentService;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public List<AppointmentResponse> listAppointments() {
    return appointmentService.listAppointments();
  }

  @GetMapping("/donor/{donorId}")
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public List<AppointmentResponse> listByDonor(@PathVariable Long donorId) {
    return appointmentService.listByDonor(donorId);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public AppointmentResponse createAppointment(@Valid @RequestBody AppointmentCreateRequest request) {
    return appointmentService.createAppointment(request);
  }
}
