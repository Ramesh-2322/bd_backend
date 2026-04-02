package com.bdms.service.impl;

import com.bdms.dto.request.BloodRequestCreateRequest;
import com.bdms.dto.request.BloodRequestResponse;
import com.bdms.dto.request.BloodRequestStatusUpdateRequest;
import com.bdms.dto.request.MatchedDonorResponse;
import com.bdms.dto.request.UserBloodRequestResponse;
import com.bdms.entity.*;
import com.bdms.exception.BadRequestException;
import com.bdms.exception.ResourceNotFoundException;
import com.bdms.repository.BloodRequestRepository;
import com.bdms.repository.DonorRepository;
import com.bdms.repository.SubscriptionRepository;
import com.bdms.service.AuditLogService;
import com.bdms.service.BloodRequestService;
import com.bdms.service.CurrentUserService;
import com.bdms.service.MatchingAsyncService;
import com.bdms.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BloodRequestServiceImpl implements BloodRequestService {

    private final BloodRequestRepository bloodRequestRepository;
    private final DonorRepository donorRepository;
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;
    private final SubscriptionRepository subscriptionRepository;
    private final AuditLogService auditLogService;
    private final MatchingAsyncService matchingAsyncService;

    @Override
    @CacheEvict(value = {"matchingDonors", "adminStats"}, allEntries = true)
    public BloodRequestResponse createRequest(BloodRequestCreateRequest request) {
        Donor currentUser = currentUserService.getCurrentUser();

        if (!currentUserService.isSuperAdmin() && currentUser.getHospital() != null) {
            var activeSubscription = subscriptionRepository.findByHospitalIdAndActiveTrue(currentUser.getHospital().getId());
            if (activeSubscription.isPresent() && activeSubscription.get().getPlanType() == SubscriptionPlan.FREE) {
                long requestCount = bloodRequestRepository.countByRequestedByHospitalId(currentUser.getHospital().getId());
                if (requestCount >= 100) {
                    throw new BadRequestException("FREE plan request limit reached for this hospital");
                }
            }
        }

        BloodRequest bloodRequest = BloodRequest.builder()
                .patientName(request.getPatientName())
                .bloodGroup(request.getBloodGroup())
                .unitsRequired(request.getUnitsRequired())
                .hospitalName(request.getHospitalName())
                .location(request.getLocation())
                .urgencyLevel(request.getUrgencyLevel())
                .status(RequestStatus.PENDING)
                .requestedBy(currentUser)
                .build();

        BloodRequest saved = bloodRequestRepository.save(bloodRequest);
        log.info("Blood request created: id={}, requestedBy={}", saved.getId(), currentUser.getEmail());
        auditLogService.log("CREATE_REQUEST", "BLOOD_REQUEST", saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "adminStats", key = "'requests:' + T(java.util.Objects).hash(#page,#size,#sortBy,#sortDir,#status,#urgencyLevel,#location,#root.target.currentUserService.getCurrentHospitalId(),#root.target.currentUserService.isSuperAdmin())")
        public Page<BloodRequestResponse> getAllRequests(int page, int size, String sortBy, String sortDir,
                                 RequestStatus status, UrgencyLevel urgencyLevel, String location) {
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
            if (currentUserService.isSuperAdmin()) {
                return bloodRequestRepository.searchAll(status, urgencyLevel, normalizeFilter(location), pageable)
                    .map(this::toResponse);
            }

            Long hospitalId = currentUserService.getCurrentHospitalId();
            return bloodRequestRepository.searchAllByHospital(hospitalId, status, urgencyLevel, normalizeFilter(location), pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
        public Page<BloodRequestResponse> getMyRequests(int page, int size, String sortBy, String sortDir,
                                RequestStatus status, UrgencyLevel urgencyLevel, String location) {
            Donor currentUser = currentUserService.getCurrentUser();
        Pageable pageable = buildPageable(page, size, sortBy, sortDir);
        return bloodRequestRepository.searchMy(currentUser.getId(), status, urgencyLevel, normalizeFilter(location), pageable)
            .map(this::toResponse);
    }

        @Override
        @Transactional(readOnly = true)
        public List<UserBloodRequestResponse> getRequestsByUserId(Long userId) {
            if (!donorRepository.existsById(userId)) {
                throw new BadRequestException("Invalid user id: " + userId);
            }

            return bloodRequestRepository.findAllByRequestedByIdOrderByCreatedAtDesc(userId)
                    .stream()
                    .map(request -> UserBloodRequestResponse.builder()
                            .id(request.getId())
                            .userId(request.getRequestedBy().getId())
                            .bloodGroup(request.getBloodGroup())
                            .units(request.getUnitsRequired())
                            .status(request.getStatus())
                            .location(request.getLocation())
                            .createdAt(request.getCreatedAt())
                            .build())
                    .toList();
        }

    @Override
    @CacheEvict(value = {"matchingDonors", "adminStats"}, allEntries = true)
    public BloodRequestResponse updateRequestStatus(Long requestId, BloodRequestStatusUpdateRequest request) {
        Donor currentUser = currentUserService.getCurrentUser();
        BloodRequest bloodRequest = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Blood request not found with id: " + requestId));

        if (!currentUserService.isSuperAdmin()) {
            if (currentUser.getHospital() == null || bloodRequest.getRequestedBy().getHospital() == null
                    || !currentUser.getHospital().getId().equals(bloodRequest.getRequestedBy().getHospital().getId())) {
                throw new AccessDeniedException("You are not allowed to update this request");
            }
        }

        RequestStatus status = request.getStatus();
        bloodRequest.setStatus(status);
        BloodRequest updated = bloodRequestRepository.save(bloodRequest);
        log.info("Blood request status updated: id={}, status={}", updated.getId(), updated.getStatus());

        if (status == RequestStatus.APPROVED) {
            notificationService.notifyRequestApproved(toResponse(updated));
            matchingAsyncService.processApprovedRequest(updated);
            auditLogService.log("APPROVE_REQUEST", "BLOOD_REQUEST", updated.getId());
        }

        return toResponse(updated);
    }

    @Override
    @CacheEvict(value = {"matchingDonors", "adminStats"}, allEntries = true)
    public void deleteRequest(Long requestId) {
        Donor currentUser = currentUserService.getCurrentUser();
        BloodRequest bloodRequest = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Blood request not found with id: " + requestId));

        boolean isOwner = bloodRequest.getRequestedBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN || currentUser.getRole() == Role.ROLE_SUPER_ADMIN;
        boolean sameHospital = currentUserService.isSuperAdmin() ||
            (currentUser.getHospital() != null && bloodRequest.getRequestedBy().getHospital() != null
                && currentUser.getHospital().getId().equals(bloodRequest.getRequestedBy().getHospital().getId()));

        if (!isOwner && (!isAdmin || !sameHospital)) {
            throw new AccessDeniedException("You are not allowed to delete this request");
        }

        bloodRequestRepository.delete(bloodRequest);
        log.info("Blood request deleted: id={}, deletedBy={}", requestId, currentUser.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "matchingDonors", key = "'match:' + #requestId + ':' + #root.target.currentUserService.getCurrentHospitalId()")
    public List<MatchedDonorResponse> getMatchingDonors(Long requestId) {
        Donor currentUser = currentUserService.getCurrentUser();

        BloodRequest bloodRequest = bloodRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Blood request not found with id: " + requestId));

        boolean isOwner = bloodRequest.getRequestedBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN || currentUser.getRole() == Role.ROLE_SUPER_ADMIN;
        boolean sameHospital = currentUserService.isSuperAdmin() ||
            (currentUser.getHospital() != null && bloodRequest.getRequestedBy().getHospital() != null
                && currentUser.getHospital().getId().equals(bloodRequest.getRequestedBy().getHospital().getId()));

        if (!isOwner && (!isAdmin || !sameHospital)) {
            throw new AccessDeniedException("You are not allowed to view matching donors for this request");
        }

        if (bloodRequest.getStatus() != RequestStatus.APPROVED) {
            throw new BadRequestException("Matching donors are available only for APPROVED requests");
        }

        List<MatchedDonorResponse> matchedDonors = getMatchingDonorsInternal(bloodRequest);
        notificationService.notifyMatchedDonors(toResponse(bloodRequest), matchedDonors);
        return matchedDonors;
    }

    private List<MatchedDonorResponse> getMatchingDonorsInternal(BloodRequest bloodRequest) {
        String requestLocation = normalize(bloodRequest.getLocation());

        return donorRepository.findAllByBloodGroupAndAvailabilityStatusTrue(bloodRequest.getBloodGroup()).stream()
            .filter(donor -> donor.getHospital() != null && bloodRequest.getRequestedBy().getHospital() != null
                && donor.getHospital().getId().equals(bloodRequest.getRequestedBy().getHospital().getId()))
                .filter(donor -> isSameOrNearbyLocation(requestLocation, normalize(donor.getLocation())))
                .map(donor -> MatchedDonorResponse.builder()
                        .id(donor.getId())
                        .name(donor.getName())
                        .email(donor.getEmail())
                        .bloodGroup(donor.getBloodGroup())
                        .location(donor.getLocation())
                        .phoneNumber(donor.getPhoneNumber())
                        .availabilityStatus(donor.getAvailabilityStatus())
                        .build())
                .toList();
    }

    private boolean isSameOrNearbyLocation(String requestLocation, String donorLocation) {
        return donorLocation.equalsIgnoreCase(requestLocation)
                || donorLocation.contains(requestLocation)
                || requestLocation.contains(donorLocation);
    }

    private String normalize(String location) {
        return location == null ? "" : location.trim().toLowerCase();
    }

    private String normalizeFilter(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }

    private BloodRequestResponse toResponse(BloodRequest bloodRequest) {
        return BloodRequestResponse.builder()
                .id(bloodRequest.getId())
                .patientName(bloodRequest.getPatientName())
                .bloodGroup(bloodRequest.getBloodGroup())
                .unitsRequired(bloodRequest.getUnitsRequired())
                .hospitalName(bloodRequest.getHospitalName())
                .location(bloodRequest.getLocation())
                .urgencyLevel(bloodRequest.getUrgencyLevel())
                .status(bloodRequest.getStatus())
                .requestedById(bloodRequest.getRequestedBy().getId())
                .requestedByName(bloodRequest.getRequestedBy().getName())
                .createdAt(bloodRequest.getCreatedAt())
                .build();
    }
}
