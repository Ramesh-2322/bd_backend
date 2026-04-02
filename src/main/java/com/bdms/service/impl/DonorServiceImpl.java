package com.bdms.service.impl;

import com.bdms.dto.donor.DonorResponse;
import com.bdms.dto.donor.DonorUpdateRequest;
import com.bdms.entity.Donor;
import com.bdms.exception.ResourceNotFoundException;
import com.bdms.repository.DonorRepository;
import com.bdms.service.CurrentUserService;
import com.bdms.service.DonorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DonorServiceImpl implements DonorService {

    private final DonorRepository donorRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Cacheable(value = "donors", key = "T(java.util.Objects).hash(#page,#size,#sortBy,#sortDir,#bloodGroup,#location,#availabilityStatus,#root.target.currentUserService.getCurrentHospitalId(),#root.target.currentUserService.isSuperAdmin())")
    public Page<DonorResponse> getAllDonors(int page, int size, String sortBy, String sortDir,
                                            String bloodGroup, String location, Boolean availabilityStatus) {
        Donor currentUser = currentUserService.getCurrentUser();
        Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (currentUserService.isSuperAdmin()) {
            return donorRepository.searchDonors(normalizeFilter(bloodGroup), normalizeFilter(location), availabilityStatus, pageable)
                .map(this::toDonorResponse);
        }

        if (currentUser.getHospital() == null) {
            return Page.empty(pageable);
        }

        return donorRepository.searchDonorsByHospital(currentUser.getHospital().getId(),
                normalizeFilter(bloodGroup), normalizeFilter(location), availabilityStatus, pageable)
            .map(this::toDonorResponse);
    }

    @Override
    public DonorResponse getCurrentDonor() {
        return toDonorResponse(currentUserService.getCurrentUser());
    }

    @Override
    public DonorResponse getDonorById(Long id) {
        Donor currentUser = currentUserService.getCurrentUser();
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donor not found with id: " + id));

        if (!currentUserService.isSuperAdmin()
                && (currentUser.getHospital() == null || donor.getHospital() == null
                || !currentUser.getHospital().getId().equals(donor.getHospital().getId()))) {
            throw new ResourceNotFoundException("Donor not found with id: " + id);
        }

        return toDonorResponse(donor);
    }

    @Override
    @CacheEvict(value = {"donors", "matchingDonors", "adminStats"}, allEntries = true)
    public DonorResponse updateCurrentAvailability(Boolean available) {
        Donor currentUser = currentUserService.getCurrentUser();
        currentUser.setAvailabilityStatus(available);
        Donor updated = donorRepository.save(currentUser);
        log.info("Donor availability updated for id={} to {}", updated.getId(), available);
        return toDonorResponse(updated);
    }

    @Override
    @CacheEvict(value = {"donors", "matchingDonors", "adminStats"}, allEntries = true)
    public DonorResponse updateDonor(Long id, DonorUpdateRequest request) {
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donor not found with id: " + id));

        donor.setName(request.getName());
        donor.setBloodGroup(request.getBloodGroup());
        donor.setLocation(request.getLocation());
        donor.setPhoneNumber(request.getPhoneNumber());
        donor.setAvailabilityStatus(request.getAvailabilityStatus());

        Donor updated = donorRepository.save(donor);
        log.info("Donor updated successfully with id={}", id);

        return toDonorResponse(updated);
    }

    @Override
    @CacheEvict(value = {"donors", "matchingDonors", "adminStats"}, allEntries = true)
    public void deleteDonor(Long id) {
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donor not found with id: " + id));
        donorRepository.delete(donor);
        log.info("Donor deleted successfully with id={}", id);
    }

    private DonorResponse toDonorResponse(Donor donor) {
        return DonorResponse.builder()
                .id(donor.getId())
                .name(donor.getName())
                .email(donor.getEmail())
                .bloodGroup(donor.getBloodGroup())
                .location(donor.getLocation())
                .phoneNumber(donor.getPhoneNumber())
                .availabilityStatus(donor.getAvailabilityStatus())
                .hospitalId(donor.getHospital() == null ? null : donor.getHospital().getId())
                .hospitalName(donor.getHospital() == null ? null : donor.getHospital().getName())
                .lastDonationDate(donor.getLastDonationDate())
                .totalDonations(donor.getTotalDonations())
                .role(donor.getRole())
                .build();
    }

    private String normalizeFilter(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
