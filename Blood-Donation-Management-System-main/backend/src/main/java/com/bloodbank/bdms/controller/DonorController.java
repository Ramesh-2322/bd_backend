package com.bloodbank.bdms.controller;

import com.bloodbank.bdms.dto.donor.DonorCreateRequest;
import com.bloodbank.bdms.dto.donor.DonorResponse;
import com.bloodbank.bdms.dto.donor.DonorUpdateRequest;
import com.bloodbank.bdms.entity.enums.BloodGroup;
import com.bloodbank.bdms.service.DonorService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donors")
public class DonorController {
  private final DonorService donorService;

  public DonorController(DonorService donorService) {
    this.donorService = donorService;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public List<DonorResponse> listDonors() {
    return donorService.listDonors();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public DonorResponse getDonor(@PathVariable Long id) {
    return donorService.getDonor(id);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public DonorResponse createDonor(@Valid @RequestBody DonorCreateRequest request) {
    return donorService.createDonor(request);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public DonorResponse updateDonor(@PathVariable Long id, @RequestBody DonorUpdateRequest request) {
    return donorService.updateDonor(id, request);
  }

  @GetMapping("/match")
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public List<DonorResponse> matchDonors(@RequestParam BloodGroup bloodGroup) {
    return donorService.matchDonors(bloodGroup);
  }
}
