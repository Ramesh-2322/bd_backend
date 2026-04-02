package com.bdms.service.impl;

import com.bdms.entity.AuditLog;
import com.bdms.entity.Donor;
import com.bdms.repository.AuditLogRepository;
import com.bdms.service.AuditLogService;
import com.bdms.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final CurrentUserService currentUserService;

    @Override
    public void log(String action, String entityType, Long entityId) {
        try {
            Donor user = currentUserService.getCurrentUser();
            AuditLog logEntry = AuditLog.builder()
                    .user(user)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .build();
            auditLogRepository.save(logEntry);
        } catch (Exception ex) {
            log.warn("Audit log write skipped. action={}, entityType={}, entityId={}", action, entityType, entityId);
        }
    }
}
