package com.bdms.service.impl;

import com.bdms.dto.audit.AuditLogResponse;
import com.bdms.entity.AuditLog;
import com.bdms.repository.AuditLogRepository;
import com.bdms.service.AuditQueryService;
import com.bdms.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditQueryServiceImpl implements AuditQueryService {

    private final AuditLogRepository auditLogRepository;
    private final CurrentUserService currentUserService;

    @Override
    public Page<AuditLogResponse> getLogs(int page, int size, String sortBy, String sortDir) {
        Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AuditLog> logs;
        if (currentUserService.isSuperAdmin()) {
            logs = auditLogRepository.findAll(pageable);
        } else {
            logs = auditLogRepository.findByUserHospitalId(currentUserService.getCurrentHospitalId(), pageable);
        }

        return logs.map(this::toResponse);
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .userId(log.getUser().getId())
                .userEmail(log.getUser().getEmail())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .timestamp(log.getTimestamp())
                .build();
    }
}
