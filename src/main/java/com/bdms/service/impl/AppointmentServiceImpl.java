package com.bdms.service.impl;

import com.bdms.dto.appointment.AppointmentCreateRequest;
import com.bdms.dto.appointment.AppointmentResponse;
import com.bdms.dto.appointment.AppointmentStatusUpdateRequest;
import com.bdms.dto.appointment.UserAppointmentResponse;
import com.bdms.entity.*;
import com.bdms.exception.BadRequestException;
import com.bdms.exception.ResourceNotFoundException;
import com.bdms.repository.AppointmentRepository;
import com.bdms.repository.BloodRequestRepository;
import com.bdms.repository.DonorRepository;
import com.bdms.service.AuditLogService;
import com.bdms.service.AppointmentService;
import com.bdms.service.CurrentUserService;
import com.bdms.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final BloodRequestRepository bloodRequestRepository;
    private final DonorRepository donorRepository;
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;
    private final AuditLogService auditLogService;

    @Override
    @CacheEvict(value = {"adminStats", "matchingDonors"}, allEntries = true)
    public AppointmentResponse bookAppointment(AppointmentCreateRequest request) {
        Donor currentDonor = currentUserService.getCurrentUser();
        BloodRequest bloodRequest = bloodRequestRepository.findById(request.getBloodRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Blood request not found with id: " + request.getBloodRequestId()));

        if (!currentUserService.isSuperAdmin()) {
            if (currentDonor.getHospital() == null || bloodRequest.getRequestedBy().getHospital() == null
                    || !currentDonor.getHospital().getId().equals(bloodRequest.getRequestedBy().getHospital().getId())) {
                throw new BadRequestException("You can only book appointments for your hospital requests");
            }
        }

        if (bloodRequest.getStatus() != RequestStatus.APPROVED) {
            throw new BadRequestException("Only APPROVED blood requests can be booked for appointments");
        }

        if (!Boolean.TRUE.equals(currentDonor.getAvailabilityStatus())) {
            throw new BadRequestException("Donor is not currently available for donation");
        }

        if (!currentDonor.getBloodGroup().equalsIgnoreCase(bloodRequest.getBloodGroup())) {
            throw new BadRequestException("Donor blood group does not match the request blood group");
        }

        boolean slotTaken = appointmentRepository.existsByDonorIdAndAppointmentDateAndStatusIn(
                currentDonor.getId(),
                request.getAppointmentDate(),
                List.of(AppointmentStatus.SCHEDULED)
        );

        if (slotTaken) {
            throw new BadRequestException("Donor already has a scheduled appointment in this time slot");
        }

        Appointment appointment = Appointment.builder()
                .donor(currentDonor)
                .bloodRequest(bloodRequest)
                .appointmentDate(request.getAppointmentDate())
                .hospitalName(request.getHospitalName())
                .status(AppointmentStatus.SCHEDULED)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        AppointmentResponse response = toResponse(saved);
        notificationService.notifyAppointmentBooked(response);

        log.info("Appointment booked: appointmentId={}, donorId={}, requestId={}, date={}",
                saved.getId(), currentDonor.getId(), bloodRequest.getId(), saved.getAppointmentDate());
        auditLogService.log("BOOK_APPOINTMENT", "APPOINTMENT", saved.getId());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getMyAppointments(int page, int size, String sortBy, String sortDir,
                                                       AppointmentStatus status, String hospitalName) {
        Donor currentDonor = currentUserService.getCurrentUser();
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        return appointmentRepository.searchMy(currentDonor.getId(), status, normalizeFilter(hospitalName), pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AppointmentResponse> getAllAppointments(int page, int size, String sortBy, String sortDir,
                                                        AppointmentStatus status, String hospitalName) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        if (currentUserService.isSuperAdmin()) {
            return appointmentRepository.searchAll(status, normalizeFilter(hospitalName), pageable)
                .map(this::toResponse);
        }

        return appointmentRepository.searchAllByHospital(currentUserService.getCurrentHospitalId(),
                status, normalizeFilter(hospitalName), pageable)
            .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAppointmentResponse> getAppointmentsByUserId(Long userId) {
        if (!donorRepository.existsById(userId)) {
            throw new BadRequestException("Invalid user id: " + userId);
        }

        return appointmentRepository.findAllByDonorIdOrderByAppointmentDateDesc(userId)
                .stream()
                .map(appointment -> UserAppointmentResponse.builder()
                        .id(appointment.getId())
                        .userId(appointment.getDonor().getId())
                        .hospitalName(appointment.getHospitalName())
                        .date(appointment.getAppointmentDate())
                        .status(appointment.getStatus())
                        .build())
                .toList();
    }

    @Override
    @CacheEvict(value = {"adminStats", "matchingDonors", "donors"}, allEntries = true)
    public AppointmentResponse updateStatus(Long appointmentId, AppointmentStatusUpdateRequest request) {
        Donor currentUser = currentUserService.getCurrentUser();
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        if (!currentUserService.isSuperAdmin()) {
            if (currentUser.getHospital() == null || appointment.getDonor().getHospital() == null
                    || !currentUser.getHospital().getId().equals(appointment.getDonor().getHospital().getId())) {
                throw new BadRequestException("You can only update appointments within your hospital");
            }
        }

        AppointmentStatus previousStatus = appointment.getStatus();
        appointment.setStatus(request.getStatus());

        if (previousStatus != AppointmentStatus.COMPLETED && request.getStatus() == AppointmentStatus.COMPLETED) {
            Donor donor = appointment.getDonor();
            donor.setLastDonationDate(LocalDate.now());
            donor.setTotalDonations((donor.getTotalDonations() == null ? 0 : donor.getTotalDonations()) + 1);
            donorRepository.save(donor);
        }

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment status updated: appointmentId={}, status={}", saved.getId(), saved.getStatus());
        auditLogService.log("UPDATE_APPOINTMENT_STATUS", "APPOINTMENT", saved.getId());
        return toResponse(saved);
    }

    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }

    private String normalizeFilter(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private AppointmentResponse toResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .donorId(appointment.getDonor().getId())
                .donorName(appointment.getDonor().getName())
                .bloodRequestId(appointment.getBloodRequest().getId())
                .patientName(appointment.getBloodRequest().getPatientName())
                .appointmentDate(appointment.getAppointmentDate())
                .hospitalName(appointment.getHospitalName())
                .status(appointment.getStatus())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
