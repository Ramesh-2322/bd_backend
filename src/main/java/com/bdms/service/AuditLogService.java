package com.bdms.service;

public interface AuditLogService {

    void log(String action, String entityType, Long entityId);
}
