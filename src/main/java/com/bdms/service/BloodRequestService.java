package com.bdms.service;

import com.bdms.dto.request.BloodRequestCreateRequest;
import com.bdms.dto.request.BloodRequestResponse;
import com.bdms.dto.request.BloodRequestStatusUpdateRequest;
import com.bdms.dto.request.MatchedDonorResponse;
import com.bdms.dto.request.UserBloodRequestResponse;
import com.bdms.entity.RequestStatus;
import com.bdms.entity.UrgencyLevel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BloodRequestService {

    BloodRequestResponse createRequest(BloodRequestCreateRequest request);

    Page<BloodRequestResponse> getAllRequests(int page, int size, String sortBy, String sortDir,
                                              RequestStatus status, UrgencyLevel urgencyLevel, String location);

    Page<BloodRequestResponse> getMyRequests(int page, int size, String sortBy, String sortDir,
                                             RequestStatus status, UrgencyLevel urgencyLevel, String location);

    List<UserBloodRequestResponse> getRequestsByUserId(Long userId);

    BloodRequestResponse updateRequestStatus(Long requestId, BloodRequestStatusUpdateRequest request);

    void deleteRequest(Long requestId);

    List<MatchedDonorResponse> getMatchingDonors(Long requestId);
}
