package com.bdms.controller;

import com.bdms.common.payload.ApiResponse;
import com.bdms.dto.appointment.AppointmentCreateRequest;
import com.bdms.dto.appointment.AppointmentResponse;
import com.bdms.dto.appointment.AppointmentStatusUpdateRequest;
import com.bdms.dto.appointment.UserAppointmentResponse;
import com.bdms.entity.AppointmentStatus;
import com.bdms.service.AppointmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponse>> bookAppointment(
            @Valid @RequestBody AppointmentCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment booked successfully",
                        appointmentService.bookAppointment(request)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<UserAppointmentResponse>>> getAppointmentsByUserId(
            @PathVariable @Min(value = 1, message = "User id must be greater than 0") Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "User appointments fetched successfully",
                appointmentService.getAppointmentsByUserId(userId)
        ));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<AppointmentResponse>>> getMyAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) String hospitalName
    ) {
        return ResponseEntity.ok(ApiResponse.success("My appointments fetched successfully",
                appointmentService.getMyAppointments(page, size, sortBy, sortDir, status, hospitalName)));
    }

    @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<AppointmentResponse>>> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) String hospitalName
    ) {
        return ResponseEntity.ok(ApiResponse.success("All appointments fetched successfully",
                appointmentService.getAllAppointments(page, size, sortBy, sortDir, status, hospitalName)));
    }

    @PutMapping("/{id}/status")
        @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Appointment status updated successfully",
                appointmentService.updateStatus(id, request)));
    }
}
