package com.bdms.service.impl;

import com.bdms.dto.admin.AdminStatsResponse;
import com.bdms.entity.AppointmentStatus;
import com.bdms.entity.SubscriptionPlan;
import com.bdms.exception.BadRequestException;
import com.bdms.repository.AppointmentRepository;
import com.bdms.repository.BloodRequestRepository;
import com.bdms.repository.DonorRepository;
import com.bdms.service.AdminService;
import com.bdms.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final DonorRepository donorRepository;
    private final BloodRequestRepository bloodRequestRepository;
    private final AppointmentRepository appointmentRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Cacheable(value = "adminStats", key = "'stats:' + #root.target.currentUserService.getCurrentHospitalId() + ':' + #root.target.currentUserService.isSuperAdmin()")
    public AdminStatsResponse getStats() {
        long totalDonors;
        long totalRequests;
        long totalCompletedDonations;
        long activeDonorsCount;

        if (currentUserService.isSuperAdmin()) {
            totalDonors = donorRepository.count();
            totalRequests = bloodRequestRepository.count();
            totalCompletedDonations = appointmentRepository.countByStatus(AppointmentStatus.COMPLETED);
            activeDonorsCount = donorRepository.countByAvailabilityStatusTrue();
        } else {
            if (currentUserService.getCurrentUser().getHospital() == null
                    || currentUserService.getCurrentUser().getHospital().getSubscriptionPlan() != SubscriptionPlan.ENTERPRISE) {
                throw new BadRequestException("Analytics is available only for ENTERPRISE plan");
            }

            Long hospitalId = currentUserService.getCurrentHospitalId();
            totalDonors = donorRepository.countByHospitalId(hospitalId);
            totalRequests = bloodRequestRepository.countByRequestedByHospitalId(hospitalId);
            totalCompletedDonations = appointmentRepository.countByDonorHospitalIdAndStatus(hospitalId, AppointmentStatus.COMPLETED);
            activeDonorsCount = donorRepository.countByHospitalIdAndAvailabilityStatusTrue(hospitalId);
        }

        return AdminStatsResponse.builder()
                .totalDonors(totalDonors)
                .totalRequests(totalRequests)
                .totalCompletedDonations(totalCompletedDonations)
                .activeDonorsCount(activeDonorsCount)
                .monthlyDonationStats(buildMonthlyStats())
                .build();
    }

    private Map<String, Long> buildMonthlyStats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate currentMonth = LocalDate.now().withDayOfMonth(1);

        for (int i = 5; i >= 0; i--) {
            LocalDate month = currentMonth.minusMonths(i);
            long count = appointmentRepository.countByStatusAndAppointmentDateBetween(
                    AppointmentStatus.COMPLETED,
                    month.atStartOfDay(),
                    month.plusMonths(1).atStartOfDay().minusSeconds(1)
            );
            stats.put(month.format(formatter), count);
        }
        return stats;
    }
}
