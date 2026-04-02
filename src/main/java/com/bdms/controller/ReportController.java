package com.bdms.controller;

import com.bdms.common.payload.ApiResponse;
import com.bdms.dto.report.ReportResponse;
import com.bdms.dto.report.ReportUploadRequest;
import com.bdms.service.ReportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Validated
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<ReportResponse>> uploadReport(@Valid @RequestBody ReportUploadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Report uploaded successfully", reportService.upload(request)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getReportsByUserId(
            @PathVariable @Min(value = 1, message = "User id must be greater than 0") Long userId
    ) {
        return ResponseEntity.ok(ApiResponse.success("User reports fetched successfully", reportService.getByUserId(userId)));
    }
}
