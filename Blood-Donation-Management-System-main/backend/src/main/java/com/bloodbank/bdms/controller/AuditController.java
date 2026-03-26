package com.bloodbank.bdms.controller;

import com.bloodbank.bdms.entity.AuditLog;
import com.bloodbank.bdms.service.AuditService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {
  private final AuditService auditService;

  public AuditController(AuditService auditService) {
    this.auditService = auditService;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public List<AuditLog> list() {
    return auditService.list();
  }
}
