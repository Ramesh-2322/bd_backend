package com.bdms.service.impl;

import com.bdms.entity.BloodRequest;
import com.bdms.entity.RequestStatus;
import com.bdms.repository.BloodRequestRepository;
import com.bdms.service.BackgroundJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackgroundJobServiceImpl implements BackgroundJobService {

    private final BloodRequestRepository bloodRequestRepository;

    @Value("${cleanup.requests.older-than-days:14}")
    private int olderThanDays;

    @Override
    @Scheduled(cron = "${cleanup.requests.cron:0 0 */6 * * *}")
    @Transactional
    public void cleanupExpiredRequests() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(olderThanDays);
        List<BloodRequest> staleRequests = bloodRequestRepository.findByStatusAndCreatedAtBefore(RequestStatus.PENDING, threshold);

        if (staleRequests.isEmpty()) {
            return;
        }

        staleRequests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
        bloodRequestRepository.saveAll(staleRequests);
        log.info("Background cleanup marked {} stale requests as REJECTED", staleRequests.size());
    }
}
