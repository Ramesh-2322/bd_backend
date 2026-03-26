package com.bloodbank.bdms.controller;

import com.bloodbank.bdms.dto.donation.DonationCreateRequest;
import com.bloodbank.bdms.dto.donation.DonationResponse;
import com.bloodbank.bdms.service.DonationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
public class DonationController {
  private final DonationService donationService;

  public DonationController(DonationService donationService) {
    this.donationService = donationService;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public List<DonationResponse> listDonations() {
    return donationService.listDonations();
  }

  @GetMapping("/donor/{donorId}")
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public List<DonationResponse> listByDonor(@PathVariable Long donorId) {
    return donationService.listByDonor(donorId);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public DonationResponse createDonation(@Valid @RequestBody DonationCreateRequest request) {
    return donationService.createDonation(request);
  }
}
