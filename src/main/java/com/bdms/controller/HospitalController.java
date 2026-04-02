package com.bdms.controller;

import com.bdms.common.payload.ApiResponse;
import com.bdms.dto.hospital.HospitalRequest;
import com.bdms.dto.hospital.HospitalResponse;
import com.bdms.service.HospitalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class HospitalController {

    private final HospitalService hospitalService;

    @PostMapping
    public ResponseEntity<ApiResponse<HospitalResponse>> create(@Valid @RequestBody HospitalRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Hospital created successfully", hospitalService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HospitalResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody HospitalRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Hospital updated successfully", hospitalService.update(id, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<HospitalResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Hospitals fetched successfully", hospitalService.getAll()));
    }
}
