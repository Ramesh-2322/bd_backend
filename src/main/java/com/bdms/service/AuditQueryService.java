package com.bdms.service;

import com.bdms.dto.audit.AuditLogResponse;
import org.springframework.data.domain.Page;

public interface AuditQueryService {

    Page<AuditLogResponse> getLogs(int page, int size, String sortBy, String sortDir);
}
