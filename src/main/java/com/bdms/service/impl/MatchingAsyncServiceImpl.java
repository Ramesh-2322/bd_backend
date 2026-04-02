package com.bdms.service.impl;

import com.bdms.dto.request.BloodRequestResponse;
import com.bdms.dto.request.MatchedDonorResponse;
import com.bdms.entity.BloodRequest;
import com.bdms.entity.Donor;
import com.bdms.repository.DonorRepository;
import com.bdms.service.MatchingAsyncService;
import com.bdms.service.NotificationService;
import com.bdms.service.RealtimeNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingAsyncServiceImpl implements MatchingAsyncService {

    private final DonorRepository donorRepository;
    private final NotificationService notificationService;
    private final RealtimeNotificationService realtimeNotificationService;

    @Override
    @Async("bdmsTaskExecutor")
    public void processApprovedRequest(BloodRequest bloodRequest) {
        String requestLocation = normalize(bloodRequest.getLocation());

        List<MatchedDonorResponse> matchedDonors = donorRepository.findAllByBloodGroupAndAvailabilityStatusTrue(bloodRequest.getBloodGroup()).stream()
                .filter(donor -> donor.getHospital() != null && bloodRequest.getRequestedBy().getHospital() != null
                        && donor.getHospital().getId().equals(bloodRequest.getRequestedBy().getHospital().getId()))
                .filter(donor -> isSameOrNearbyLocation(requestLocation, normalize(donor.getLocation())))
                .map(this::toMatchedDonor)
                .toList();

        BloodRequestResponse response = toResponse(bloodRequest);
        notificationService.notifyMatchedDonors(response, matchedDonors);
        realtimeNotificationService.publish("/topic/requests/approved", response);
        realtimeNotificationService.publish("/topic/requests/matched-donors", matchedDonors);

        log.info("Async matching completed for requestId={}, matchedDonors={}", bloodRequest.getId(), matchedDonors.size());
    }

    private MatchedDonorResponse toMatchedDonor(Donor donor) {
        return MatchedDonorResponse.builder()
                .id(donor.getId())
                .name(donor.getName())
                .email(donor.getEmail())
                .bloodGroup(donor.getBloodGroup())
                .location(donor.getLocation())
                .phoneNumber(donor.getPhoneNumber())
                .availabilityStatus(donor.getAvailabilityStatus())
                .build();
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

    private boolean isSameOrNearbyLocation(String requestLocation, String donorLocation) {
        return donorLocation.equalsIgnoreCase(requestLocation)
                || donorLocation.contains(requestLocation)
                || requestLocation.contains(donorLocation);
    }

    private String normalize(String location) {
        return location == null ? "" : location.trim().toLowerCase();
    }
}
