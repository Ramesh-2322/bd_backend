package com.bdms.service;

import com.bdms.dto.file.MedicalReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface MedicalReportService {

    MedicalReportResponse upload(MultipartFile file);

    Page<MedicalReportResponse> getMyReports(int page, int size, String sortBy, String sortDir);
}
