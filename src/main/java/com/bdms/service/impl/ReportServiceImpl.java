package com.bdms.service.impl;

import com.bdms.dto.report.ReportResponse;
import com.bdms.dto.report.ReportUploadRequest;
import com.bdms.entity.Report;
import com.bdms.exception.BadRequestException;
import com.bdms.repository.DonorRepository;
import com.bdms.repository.ReportRepository;
import com.bdms.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final DonorRepository donorRepository;

    @Override
    public ReportResponse upload(ReportUploadRequest request) {
        if (!donorRepository.existsById(request.getUserId())) {
            throw new BadRequestException("Invalid user id: " + request.getUserId());
        }

        Report saved = reportRepository.save(Report.builder()
                .userId(request.getUserId())
                .fileUrl(request.getFileUrl().trim())
                .build());

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportResponse> getByUserId(Long userId) {
        return reportRepository.findByUserIdOrderByUploadedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReportResponse toResponse(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .userId(report.getUserId())
                .fileUrl(report.getFileUrl())
                .uploadedAt(report.getUploadedAt())
                .build();
    }
}
