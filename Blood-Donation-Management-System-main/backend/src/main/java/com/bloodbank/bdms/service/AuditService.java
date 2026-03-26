package com.bloodbank.bdms.service;

import com.bloodbank.bdms.entity.AuditLog;
import com.bloodbank.bdms.entity.User;
import com.bloodbank.bdms.repository.AuditLogRepository;
import com.bloodbank.bdms.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditService {
  private final AuditLogRepository auditRepository;
  private final UserRepository userRepository;

  public AuditService(AuditLogRepository auditRepository, UserRepository userRepository) {
    this.auditRepository = auditRepository;
    this.userRepository = userRepository;
  }

  public List<AuditLog> list() {
    return auditRepository.findAll();
  }

  @Transactional
  public void record(String action, String entityType, Long entityId, String metadata) {
    User actor = getCurrentUser();
    AuditLog log = AuditLog.builder()
        .actor(actor)
        .action(action)
        .entityType(entityType)
        .entityId(entityId)
        .metadata(metadata)
        .build();
    auditRepository.save(log);
  }

  private User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getName() == null) {
      return null;
    }
    return userRepository.findByEmail(auth.getName()).orElse(null);
  }
}
