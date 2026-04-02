package com.bdms.controller;

import com.bdms.common.payload.ApiResponse;
import com.bdms.dto.donor.AvailabilityUpdateRequest;
import com.bdms.dto.donor.DonorResponse;
import com.bdms.dto.donor.DonorUpdateRequest;
import com.bdms.service.DonorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/donors")
@RequiredArgsConstructor
public class DonorController {

    private final DonorService donorService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DonorResponse>>> getAllDonors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean availabilityStatus
    ) {
        return ResponseEntity.ok(ApiResponse.success("Donors fetched successfully",
                donorService.getAllDonors(page, size, sortBy, sortDir, bloodGroup, location, availabilityStatus)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<DonorResponse>> getCurrentDonor() {
        return ResponseEntity.ok(ApiResponse.success("Donor fetched successfully", donorService.getCurrentDonor()));
    }

    @PatchMapping("/me/availability")
    public ResponseEntity<ApiResponse<DonorResponse>> updateCurrentAvailability(
            @Valid @RequestBody AvailabilityUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Availability updated successfully",
                donorService.updateCurrentAvailability(request.getAvailable())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DonorResponse>> getDonorById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Donor fetched successfully", donorService.getDonorById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DonorResponse>> updateDonor(
            @PathVariable Long id,
            @Valid @RequestBody DonorUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Donor updated successfully", donorService.updateDonor(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteDonor(@PathVariable Long id) {
        donorService.deleteDonor(id);
        return ResponseEntity.ok(ApiResponse.success("Donor deleted successfully", null));
    }
}
