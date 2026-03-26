package com.bloodbank.bdms.service;

import com.bloodbank.bdms.dto.request.BloodRequestCreateRequest;
import com.bloodbank.bdms.dto.request.BloodRequestResponse;
import com.bloodbank.bdms.dto.request.BloodRequestUpdateRequest;
import com.bloodbank.bdms.entity.BloodRequest;
import com.bloodbank.bdms.entity.enums.NotificationType;
import com.bloodbank.bdms.entity.enums.RequestStatus;
import com.bloodbank.bdms.entity.enums.RoleName;
import com.bloodbank.bdms.exception.NotFoundException;
import com.bloodbank.bdms.repository.BloodRequestRepository;
import com.bloodbank.bdms.repository.DonorProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestService {
  private final BloodRequestRepository requestRepository;
  private final DonorProfileRepository donorRepository;
  private final NotificationService notificationService;
  private final InventoryService inventoryService;
  private final AuditService auditService;

  public RequestService(
      BloodRequestRepository requestRepository,
      DonorProfileRepository donorRepository,
      NotificationService notificationService,
      InventoryService inventoryService,
      AuditService auditService
  ) {
    this.requestRepository = requestRepository;
    this.donorRepository = donorRepository;
    this.notificationService = notificationService;
    this.inventoryService = inventoryService;
    this.auditService = auditService;
  }

  public List<BloodRequestResponse> listRequests() {
    return requestRepository.findAll().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  public BloodRequestResponse getRequest(Long id) {
    BloodRequest request = requestRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Request not found"));
    return toResponse(request);
  }

  @Transactional
  public BloodRequestResponse createRequest(BloodRequestCreateRequest request) {
    int matched = donorRepository.findByBloodGroupAndAvailableTrue(request.getBloodGroup()).size();

    BloodRequest entity = BloodRequest.builder()
        .requesterName(request.getRequesterName())
        .requesterContact(request.getRequesterContact())
        .bloodGroup(request.getBloodGroup())
        .unitsNeeded(request.getUnitsNeeded())
        .priority(request.getPriority())
        .hospital(request.getHospital())
        .status(matched > 0 ? RequestStatus.MATCHED : RequestStatus.OPEN)
        .neededBy(request.getNeededBy())
        .matchedDonors(matched)
        .build();

    BloodRequest saved = requestRepository.save(entity);

    notificationService.notifyRole(RoleName.ROLE_ADMIN, "New Blood Request",
        "New request for " + request.getBloodGroup() + " at " + request.getHospital(), NotificationType.REQUEST);
    notificationService.notifyRole(RoleName.ROLE_STAFF, "New Blood Request",
        "New request for " + request.getBloodGroup() + " at " + request.getHospital(), NotificationType.REQUEST);

    auditService.record("CREATE_REQUEST", "BloodRequest", saved.getId(), "priority=" + saved.getPriority());
    return toResponse(saved);
  }

  @Transactional
  public BloodRequestResponse updateRequest(Long id, BloodRequestUpdateRequest request) {
    BloodRequest entity = requestRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Request not found"));

    if (request.getStatus() != null) entity.setStatus(request.getStatus());
    if (request.getMatchedDonors() != null) entity.setMatchedDonors(request.getMatchedDonors());
    if (request.getUnitsNeeded() != null) entity.setUnitsNeeded(request.getUnitsNeeded());
    if (request.getPriority() != null) entity.setPriority(request.getPriority());
    if (request.getHospital() != null) entity.setHospital(request.getHospital());
    if (request.getNeededBy() != null) entity.setNeededBy(request.getNeededBy());

    if (request.getStatus() == RequestStatus.FULFILLED && entity.getUnitsNeeded() > 0) {
      inventoryService.consumeUnits(entity.getBloodGroup(), entity.getUnitsNeeded());
    }

    auditService.record("UPDATE_REQUEST", "BloodRequest", entity.getId(), "status=" + entity.getStatus());
    return toResponse(entity);
  }

  private BloodRequestResponse toResponse(BloodRequest request) {
    return BloodRequestResponse.builder()
        .id(request.getId())
        .requesterName(request.getRequesterName())
        .requesterContact(request.getRequesterContact())
        .bloodGroup(request.getBloodGroup())
        .unitsNeeded(request.getUnitsNeeded())
        .priority(request.getPriority())
        .hospital(request.getHospital())
        .status(request.getStatus())
        .neededBy(request.getNeededBy())
        .matchedDonors(request.getMatchedDonors())
        .createdAt(request.getCreatedAt())
        .build();
  }
}
