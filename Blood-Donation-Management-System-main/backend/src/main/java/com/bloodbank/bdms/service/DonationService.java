package com.bloodbank.bdms.service;

import com.bloodbank.bdms.dto.donation.DonationCreateRequest;
import com.bloodbank.bdms.dto.donation.DonationResponse;
import com.bloodbank.bdms.entity.Donation;
import com.bloodbank.bdms.entity.DonorProfile;
import com.bloodbank.bdms.entity.enums.DonationStatus;
import com.bloodbank.bdms.exception.NotFoundException;
import com.bloodbank.bdms.repository.DonationRepository;
import com.bloodbank.bdms.repository.DonorProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationService {
  private final DonationRepository donationRepository;
  private final DonorProfileRepository donorRepository;
  private final InventoryService inventoryService;
  private final AuditService auditService;

  public DonationService(
      DonationRepository donationRepository,
      DonorProfileRepository donorRepository,
      InventoryService inventoryService,
      AuditService auditService
  ) {
    this.donationRepository = donationRepository;
    this.donorRepository = donorRepository;
    this.inventoryService = inventoryService;
    this.auditService = auditService;
  }

  public List<DonationResponse> listDonations() {
    return donationRepository.findAll().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  public List<DonationResponse> listByDonor(Long donorId) {
    return donationRepository.findByDonorId(donorId).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public DonationResponse createDonation(DonationCreateRequest request) {
    DonorProfile donor = donorRepository.findById(request.getDonorId())
        .orElseThrow(() -> new NotFoundException("Donor not found"));

    Donation donation = Donation.builder()
        .donor(donor)
        .donationDate(request.getDonationDate())
        .quantityMl(request.getQuantityMl())
        .center(request.getCenter())
        .status(request.getStatus())
        .notes(request.getNotes())
        .build();

    if (request.getStatus() == DonationStatus.COMPLETED) {
      donor.setLastDonationDate(request.getDonationDate());
      donor.setNextEligibleDate(request.getDonationDate().plusDays(90));
      donor.setTotalDonations(donor.getTotalDonations() + 1);
      inventoryService.addDonation(donor.getBloodGroup(), request.getQuantityMl());
    }

    Donation saved = donationRepository.save(donation);
    auditService.record("CREATE_DONATION", "Donation", saved.getId(), "quantity=" + saved.getQuantityMl());
    return toResponse(saved);
  }

  private DonationResponse toResponse(Donation donation) {
    DonorProfile donor = donation.getDonor();
    return DonationResponse.builder()
        .id(donation.getId())
        .donorId(donor.getId())
        .donorName(donor.getUser() != null ? donor.getUser().getName() : "Unknown")
        .donationDate(donation.getDonationDate())
        .quantityMl(donation.getQuantityMl())
        .center(donation.getCenter())
        .status(donation.getStatus())
        .notes(donation.getNotes())
        .build();
  }
}
