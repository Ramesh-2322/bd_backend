package com.bdms.service.impl;

import com.bdms.dto.appointment.AppointmentResponse;
import com.bdms.dto.request.BloodRequestResponse;
import com.bdms.dto.request.MatchedDonorResponse;
import com.bdms.service.NotificationService;
import com.bdms.service.RealtimeNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender javaMailSender;
    private final RealtimeNotificationService realtimeNotificationService;

    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${notification.email.from:noreply@bdms.local}")
    private String fromEmail;

    @Value("${notification.email.admin-recipient:admin@bdms.local}")
    private String adminRecipient;

    @Override
    @Async("bdmsTaskExecutor")
    public void notifyRequestApproved(BloodRequestResponse bloodRequest) {
        String subject = "Blood Request Approved - Request #" + bloodRequest.getId();
        String body = "Your blood request for patient " + bloodRequest.getPatientName() + " has been approved.";

        sendEmail(adminRecipient, subject, body);
        realtimeNotificationService.publish("/topic/requests/approved", bloodRequest);
        log.info("Request approval notification triggered for requestId={}", bloodRequest.getId());
    }

    @Override
    @Async("bdmsTaskExecutor")
    public void notifyMatchedDonors(BloodRequestResponse bloodRequest, List<MatchedDonorResponse> matchedDonors) {
        log.info("Notification trigger for requestId={}, matchedDonorsCount={}", bloodRequest.getId(), matchedDonors.size());

        for (MatchedDonorResponse donor : matchedDonors) {
            sendEmail(
                    donor.getEmail(),
                    "Urgent Blood Match Opportunity",
                    "You have been matched for blood request #" + bloodRequest.getId() +
                            " at " + bloodRequest.getHospitalName() + "."
            );
            log.info("Matched donor notification simulated -> requestId={}, donorId={}, donorEmail={}, donorPhone={}",
                    bloodRequest.getId(), donor.getId(), donor.getEmail(), donor.getPhoneNumber());
        }

        realtimeNotificationService.publish("/topic/requests/matched-donors", matchedDonors);
    }

    @Override
    @Async("bdmsTaskExecutor")
    public void notifyAppointmentBooked(AppointmentResponse appointment) {
        String subject = "Appointment Booked - Appointment #" + appointment.getId();
        String body = "Appointment scheduled on " + appointment.getAppointmentDate() +
                " at " + appointment.getHospitalName() + " for patient " + appointment.getPatientName() + ".";

        sendEmail(adminRecipient, subject, body);
        realtimeNotificationService.publish("/topic/appointments/booked", appointment);
        log.info("Appointment booked notification triggered for appointmentId={}", appointment.getId());
    }

    private void sendEmail(String to, String subject, String body) {
        if (!emailEnabled) {
            log.info("Email disabled. Simulated email -> to={}, subject={}", to, subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            javaMailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (Exception ex) {
            log.error("Failed to send email to {}. Falling back to log-only notification", to, ex);
        }
    }
}
