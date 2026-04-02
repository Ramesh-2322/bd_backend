package com.bdms.service;

import com.bdms.entity.BloodRequest;

public interface MatchingAsyncService {

    void processApprovedRequest(BloodRequest bloodRequest);
}
