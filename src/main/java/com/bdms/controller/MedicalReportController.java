package com.bdms.controller;

import com.bdms.common.payload.ApiResponse;
import com.bdms.dto.file.MedicalReportResponse;
import com.bdms.service.MedicalReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class MedicalReportController {

    private final MedicalReportService medicalReportService;

    @PostMapping(value = "/medical-reports", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<MedicalReportResponse>> uploadMedicalReport(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success("Medical report uploaded successfully", medicalReportService.upload(file)));
    }

    @GetMapping("/medical-reports/my")
    public ResponseEntity<ApiResponse<Page<MedicalReportResponse>>> getMyReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ResponseEntity.ok(ApiResponse.success("Medical reports fetched successfully",
                medicalReportService.getMyReports(page, size, sortBy, sortDir)));
    }
}
