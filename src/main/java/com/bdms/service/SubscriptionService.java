package com.bdms.service;

import com.bdms.dto.subscription.SubscriptionRequest;
import com.bdms.dto.subscription.SubscriptionResponse;

import java.util.List;

public interface SubscriptionService {

    SubscriptionResponse createOrUpdate(SubscriptionRequest request);

    List<SubscriptionResponse> getByHospital(Long hospitalId);
}
