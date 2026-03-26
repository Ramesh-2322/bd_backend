package com.bloodbank.bdms.service;

import com.bloodbank.bdms.dto.appointment.AppointmentCreateRequest;
import com.bloodbank.bdms.dto.appointment.AppointmentResponse;
import com.bloodbank.bdms.entity.Appointment;
import com.bloodbank.bdms.entity.DonorProfile;
import com.bloodbank.bdms.entity.enums.AppointmentStatus;
import com.bloodbank.bdms.exception.NotFoundException;
import com.bloodbank.bdms.repository.AppointmentRepository;
import com.bloodbank.bdms.repository.DonorProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
  private final AppointmentRepository appointmentRepository;
  private final DonorProfileRepository donorRepository;
  private final AuditService auditService;

  public AppointmentService(AppointmentRepository appointmentRepository, DonorProfileRepository donorRepository,
                            AuditService auditService) {
    this.appointmentRepository = appointmentRepository;
    this.donorRepository = donorRepository;
    this.auditService = auditService;
  }

  public List<AppointmentResponse> listAppointments() {
    return appointmentRepository.findAll().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  public List<AppointmentResponse> listByDonor(Long donorId) {
    return appointmentRepository.findByDonorId(donorId).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public AppointmentResponse createAppointment(AppointmentCreateRequest request) {
    DonorProfile donor = donorRepository.findById(request.getDonorId())
        .orElseThrow(() -> new NotFoundException("Donor not found"));

    Appointment appointment = Appointment.builder()
        .donor(donor)
        .scheduledAt(request.getScheduledAt())
        .location(request.getLocation())
        .status(AppointmentStatus.SCHEDULED)
        .notes(request.getNotes())
        .build();

    Appointment saved = appointmentRepository.save(appointment);
    auditService.record("CREATE_APPOINTMENT", "Appointment", saved.getId(), "scheduledAt=" + saved.getScheduledAt());
    return toResponse(saved);
  }

  private AppointmentResponse toResponse(Appointment appointment) {
    DonorProfile donor = appointment.getDonor();
    return AppointmentResponse.builder()
        .id(appointment.getId())
        .donorId(donor.getId())
        .donorName(donor.getUser() != null ? donor.getUser().getName() : "Unknown")
        .scheduledAt(appointment.getScheduledAt())
        .location(appointment.getLocation())
        .status(appointment.getStatus())
        .notes(appointment.getNotes())
        .build();
  }
}
