package com.bdms.service;

import com.bdms.dto.request.BloodRequestResponse;
import com.bdms.dto.request.MatchedDonorResponse;
import com.bdms.dto.appointment.AppointmentResponse;

import java.util.List;

public interface NotificationService {

    void notifyRequestApproved(BloodRequestResponse bloodRequest);

    void notifyMatchedDonors(BloodRequestResponse bloodRequest, List<MatchedDonorResponse> matchedDonors);

    void notifyAppointmentBooked(AppointmentResponse appointment);
}
