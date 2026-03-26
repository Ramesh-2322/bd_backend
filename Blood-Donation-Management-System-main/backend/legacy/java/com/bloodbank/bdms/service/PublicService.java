package com.bloodbank.bdms.service;

import com.bloodbank.bdms.dto.BloodGroupResponse;
import com.bloodbank.bdms.dto.BloodRequestCreate;
import com.bloodbank.bdms.dto.BloodRequestResponse;
import com.bloodbank.bdms.entity.BloodGroup;
import com.bloodbank.bdms.entity.BloodRequest;
import com.bloodbank.bdms.entity.DonorProfile;
import com.bloodbank.bdms.repository.BloodGroupRepository;
import com.bloodbank.bdms.repository.BloodRequestRepository;
import com.bloodbank.bdms.repository.DonorProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicService {
    private final BloodGroupRepository bloodGroupRepository;
    private final DonorProfileRepository donorRepository;
    private final BloodRequestRepository requestRepository;
    private final DonorService donorService;

    public PublicService(BloodGroupRepository bloodGroupRepository,
                         DonorProfileRepository donorRepository,
                         BloodRequestRepository requestRepository,
                         DonorService donorService) {
        this.bloodGroupRepository = bloodGroupRepository;
        this.donorRepository = donorRepository;
        this.requestRepository = requestRepository;
        this.donorService = donorService;
    }

    public List<BloodGroupResponse> getBloodGroups() {
        return bloodGroupRepository.findAll().stream()
                .map(bg -> new BloodGroupResponse(bg.getId(), bg.getName(), donorRepository.countByBloodGroup(bg)))
                .collect(Collectors.toList());
    }

    public List<DonorProfile> getDonors(Long bloodGroupId, boolean readyOnly) {
        if (bloodGroupId == null) {
            return readyOnly ? donorRepository.findByReadyToDonateTrue() : donorRepository.findAll();
        }
        BloodGroup bg = bloodGroupRepository.findById(bloodGroupId).orElseThrow();
        return readyOnly ? donorRepository.findByBloodGroupAndReadyToDonateTrue(bg) : donorRepository.findByBloodGroup(bg);
    }

    public DonorProfile getDonorById(Long id) {
        return donorRepository.findById(id).orElseThrow();
    }

    public BloodRequestResponse createRequest(BloodRequestCreate request) {
        BloodGroup bg = bloodGroupRepository.findByName(request.bloodGroup()).orElseThrow();
        BloodRequest entity = new BloodRequest();
        entity.setName(request.name());
        entity.setEmail(request.email());
        entity.setPhone(request.phone());
        entity.setState(request.state());
        entity.setCity(request.city());
        entity.setAddress(request.address());
        entity.setBloodGroup(bg);
        entity.setRequestDate(request.requestDate());
        entity = requestRepository.save(entity);
        return new BloodRequestResponse(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getState(),
                entity.getCity(),
                entity.getAddress(),
                entity.getBloodGroup().getName(),
                entity.getRequestDate()
        );
    }

    public List<BloodRequestResponse> getRequests() {
        return requestRepository.findAll().stream()
                .map(r -> new BloodRequestResponse(
                        r.getId(),
                        r.getName(),
                        r.getEmail(),
                        r.getPhone(),
                        r.getState(),
                        r.getCity(),
                        r.getAddress(),
                        r.getBloodGroup().getName(),
                        r.getRequestDate()
                ))
                .collect(Collectors.toList());
    }

    public DonorService getDonorService() {
        return donorService;
    }
}
