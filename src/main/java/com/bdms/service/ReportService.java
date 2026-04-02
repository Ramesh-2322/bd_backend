package com.bdms.service;

import com.bdms.dto.report.ReportResponse;
import com.bdms.dto.report.ReportUploadRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReportService {

    ReportResponse upload(MultipartFile file, Long userId);

    ReportResponse upload(ReportUploadRequest request);

    List<ReportResponse> list(Long userId);

    List<ReportResponse> getByUserId(Long userId);
}
