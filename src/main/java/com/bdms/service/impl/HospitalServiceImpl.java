package com.bdms.service.impl;

import com.bdms.dto.hospital.HospitalRequest;
import com.bdms.dto.hospital.HospitalResponse;
import com.bdms.entity.Hospital;
import com.bdms.exception.BadRequestException;
import com.bdms.exception.ResourceNotFoundException;
import com.bdms.repository.HospitalRepository;
import com.bdms.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HospitalServiceImpl implements HospitalService {

    private final HospitalRepository hospitalRepository;

    @Override
    public HospitalResponse create(HospitalRequest request) {
        if (hospitalRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Hospital email already exists");
        }

        Hospital saved = hospitalRepository.save(Hospital.builder()
                .name(request.getName())
                .location(request.getLocation())
                .email(request.getEmail())
                .subscriptionPlan(request.getSubscriptionPlan())
                .build());

        return toResponse(saved);
    }

    @Override
    public HospitalResponse update(Long id, HospitalRequest request) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + id));

        hospital.setName(request.getName());
        hospital.setLocation(request.getLocation());
        hospital.setEmail(request.getEmail());
        hospital.setSubscriptionPlan(request.getSubscriptionPlan());

        return toResponse(hospitalRepository.save(hospital));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HospitalResponse> getAll() {
        return hospitalRepository.findAll().stream().map(this::toResponse).toList();
    }

    private HospitalResponse toResponse(Hospital hospital) {
        return HospitalResponse.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .location(hospital.getLocation())
                .email(hospital.getEmail())
                .subscriptionPlan(hospital.getSubscriptionPlan())
                .build();
    }
}
