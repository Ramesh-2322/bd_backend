package com.bloodbank.bdms.service;

import com.bloodbank.bdms.dto.donor.DonorCreateRequest;
import com.bloodbank.bdms.dto.donor.DonorResponse;
import com.bloodbank.bdms.dto.donor.DonorUpdateRequest;
import com.bloodbank.bdms.entity.DonorProfile;
import com.bloodbank.bdms.entity.Role;
import com.bloodbank.bdms.entity.User;
import com.bloodbank.bdms.entity.enums.BloodGroup;
import com.bloodbank.bdms.entity.enums.RoleName;
import com.bloodbank.bdms.entity.enums.UserStatus;
import com.bloodbank.bdms.exception.ApiException;
import com.bloodbank.bdms.exception.NotFoundException;
import com.bloodbank.bdms.repository.DonorProfileRepository;
import com.bloodbank.bdms.repository.RoleRepository;
import com.bloodbank.bdms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonorService {
  private final DonorProfileRepository donorRepository;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuditService auditService;

  public DonorService(
      DonorProfileRepository donorRepository,
      UserRepository userRepository,
      RoleRepository roleRepository,
      PasswordEncoder passwordEncoder,
      AuditService auditService
  ) {
    this.donorRepository = donorRepository;
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.auditService = auditService;
  }

  public List<DonorResponse> listDonors() {
    return donorRepository.findAll().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  public DonorResponse getDonor(Long id) {
    DonorProfile donor = donorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Donor not found"));
    return toResponse(donor);
  }

  public List<DonorResponse> matchDonors(BloodGroup bloodGroup) {
    return donorRepository.findByBloodGroupAndAvailableTrue(bloodGroup).stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public DonorResponse createDonor(DonorCreateRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new ApiException("Email already registered");
    }

    Role donorRole = roleRepository.findByName(RoleName.ROLE_DONOR)
        .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_DONOR).build()));

    User user = User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .phone(request.getPhone())
        .passwordHash(passwordEncoder.encode(request.getPassword()))
        .status(UserStatus.ACTIVE)
        .build();
    user.getRoles().add(donorRole);
    user = userRepository.save(user);

    DonorProfile donor = DonorProfile.builder()
        .user(user)
        .bloodGroup(request.getBloodGroup())
        .gender(request.getGender())
        .dateOfBirth(request.getDateOfBirth())
        .address(request.getAddress())
        .city(request.getCity())
        .available(request.isAvailable())
        .totalDonations(0)
        .build();

    DonorProfile saved = donorRepository.save(donor);
    auditService.record("CREATE_DONOR", "DonorProfile", saved.getId(), "bloodGroup=" + saved.getBloodGroup());
    return toResponse(saved);
  }

  @Transactional
  public DonorResponse updateDonor(Long id, DonorUpdateRequest request) {
    DonorProfile donor = donorRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Donor not found"));

    if (request.getName() != null) donor.getUser().setName(request.getName());
    if (request.getPhone() != null) donor.getUser().setPhone(request.getPhone());
    if (request.getBloodGroup() != null) donor.setBloodGroup(request.getBloodGroup());
    if (request.getGender() != null) donor.setGender(request.getGender());
    if (request.getDateOfBirth() != null) donor.setDateOfBirth(request.getDateOfBirth());
    if (request.getAddress() != null) donor.setAddress(request.getAddress());
    if (request.getCity() != null) donor.setCity(request.getCity());
    if (request.getLastDonationDate() != null) donor.setLastDonationDate(request.getLastDonationDate());
    if (request.getNextEligibleDate() != null) donor.setNextEligibleDate(request.getNextEligibleDate());
    if (request.getAvailable() != null) donor.setAvailable(request.getAvailable());
    if (request.getTotalDonations() != null) donor.setTotalDonations(request.getTotalDonations());

    auditService.record("UPDATE_DONOR", "DonorProfile", donor.getId(), "updated=true");
    return toResponse(donor);
  }

  private DonorResponse toResponse(DonorProfile donor) {
    User user = donor.getUser();
    return DonorResponse.builder()
        .id(donor.getId())
        .userId(user != null ? user.getId() : null)
        .name(user != null ? user.getName() : "Unknown")
        .email(user != null ? user.getEmail() : "-")
        .phone(user != null ? user.getPhone() : "-")
        .bloodGroup(donor.getBloodGroup())
        .gender(donor.getGender())
        .dateOfBirth(donor.getDateOfBirth())
        .address(donor.getAddress())
        .city(donor.getCity())
        .lastDonationDate(donor.getLastDonationDate())
        .nextEligibleDate(donor.getNextEligibleDate())
        .available(donor.isAvailable())
        .totalDonations(donor.getTotalDonations())
        .createdAt(donor.getCreatedAt())
        .build();
  }
}
