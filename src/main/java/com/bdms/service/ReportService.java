package com.bdms.service;

import com.bdms.dto.report.ReportResponse;
import com.bdms.dto.report.ReportUploadRequest;

import java.util.List;

public interface ReportService {

    ReportResponse upload(ReportUploadRequest request);

    List<ReportResponse> getByUserId(Long userId);
}
