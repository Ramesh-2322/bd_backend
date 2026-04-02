package com.bdms.service;

import com.bdms.dto.donor.DonorResponse;
import com.bdms.dto.donor.DonorUpdateRequest;
import org.springframework.data.domain.Page;

public interface DonorService {
    Page<DonorResponse> getAllDonors(int page, int size, String sortBy, String sortDir,
                                     String bloodGroup, String location, Boolean availabilityStatus);

    DonorResponse getCurrentDonor();

    DonorResponse getDonorById(Long id);

    DonorResponse updateCurrentAvailability(Boolean available);

    DonorResponse updateDonor(Long id, DonorUpdateRequest request);

    void deleteDonor(Long id);
}
