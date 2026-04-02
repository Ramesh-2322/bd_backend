package com.bdms.service.impl;

import com.bdms.dto.subscription.SubscriptionRequest;
import com.bdms.dto.subscription.SubscriptionResponse;
import com.bdms.entity.Hospital;
import com.bdms.entity.Subscription;
import com.bdms.exception.ResourceNotFoundException;
import com.bdms.repository.HospitalRepository;
import com.bdms.repository.SubscriptionRepository;
import com.bdms.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final HospitalRepository hospitalRepository;

    @Override
    @CacheEvict(value = {"adminStats", "donors", "matchingDonors"}, allEntries = true)
    public SubscriptionResponse createOrUpdate(SubscriptionRequest request) {
        Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                .orElseThrow(() -> new ResourceNotFoundException("Hospital not found with id: " + request.getHospitalId()));

        Subscription subscription = Subscription.builder()
                .hospital(hospital)
                .planType(request.getPlanType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(request.getActive())
                .build();

        hospital.setSubscriptionPlan(request.getPlanType());
        hospitalRepository.save(hospital);

        return toResponse(subscriptionRepository.save(subscription));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getByHospital(Long hospitalId) {
        return subscriptionRepository.findByHospitalIdOrderByStartDateDesc(hospitalId).stream()
                .map(this::toResponse)
                .toList();
    }

    private SubscriptionResponse toResponse(Subscription subscription) {
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .hospitalId(subscription.getHospital().getId())
                .hospitalName(subscription.getHospital().getName())
                .planType(subscription.getPlanType())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .active(subscription.getActive())
                .build();
    }
}
