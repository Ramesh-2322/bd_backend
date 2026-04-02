package com.bdms.controller;

import com.bdms.common.payload.ApiResponse;
import com.bdms.dto.request.BloodRequestCreateRequest;
import com.bdms.dto.request.BloodRequestResponse;
import com.bdms.dto.request.BloodRequestStatusUpdateRequest;
import com.bdms.dto.request.MatchedDonorResponse;
import com.bdms.dto.request.UserBloodRequestResponse;
import com.bdms.entity.RequestStatus;
import com.bdms.entity.UrgencyLevel;
import com.bdms.service.BloodRequestService;
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
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@Validated
public class BloodRequestController {

    private final BloodRequestService bloodRequestService;

    @PostMapping
    public ResponseEntity<ApiResponse<BloodRequestResponse>> createRequest(
            @Valid @RequestBody BloodRequestCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Blood request created successfully", bloodRequestService.createRequest(request)));
    }

        @GetMapping("/user/{userId}")
        public ResponseEntity<ApiResponse<List<UserBloodRequestResponse>>> getRequestsByUserId(
            @PathVariable @Min(value = 1, message = "User id must be greater than 0") Long userId
        ) {
        return ResponseEntity.ok(ApiResponse.success(
            "User blood requests fetched successfully",
            bloodRequestService.getRequestsByUserId(userId)
        ));
        }

    @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','HOSPITAL')")
    public ResponseEntity<ApiResponse<Page<BloodRequestResponse>>> getAllRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) UrgencyLevel urgencyLevel,
            @RequestParam(required = false) String location
    ) {
        return ResponseEntity.ok(ApiResponse.success("All blood requests fetched successfully",
                bloodRequestService.getAllRequests(page, size, sortBy, sortDir, status, urgencyLevel, location)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<Page<BloodRequestResponse>>> getMyRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) UrgencyLevel urgencyLevel,
            @RequestParam(required = false) String location
    ) {
        return ResponseEntity.ok(ApiResponse.success("My blood requests fetched successfully",
                bloodRequestService.getMyRequests(page, size, sortBy, sortDir, status, urgencyLevel, location)));
    }

    @PutMapping("/{id}/status")
        @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','HOSPITAL')")
    public ResponseEntity<ApiResponse<BloodRequestResponse>> updateRequestStatus(
            @PathVariable Long id,
            @Valid @RequestBody BloodRequestStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Blood request status updated successfully",
                bloodRequestService.updateRequestStatus(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteRequest(@PathVariable Long id) {
        bloodRequestService.deleteRequest(id);
        return ResponseEntity.ok(ApiResponse.success("Blood request deleted successfully", null));
    }

    @GetMapping("/{id}/matching-donors")
    public ResponseEntity<ApiResponse<List<MatchedDonorResponse>>> getMatchingDonors(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Matching donors fetched successfully",
                bloodRequestService.getMatchingDonors(id)));
    }
}
