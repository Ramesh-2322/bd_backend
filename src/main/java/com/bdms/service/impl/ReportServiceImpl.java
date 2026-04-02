package com.bdms.service.impl;

import com.bdms.dto.report.ReportResponse;
import com.bdms.dto.report.ReportUploadRequest;
import com.bdms.entity.Donor;
import com.bdms.entity.Report;
import com.bdms.entity.Role;
import com.bdms.exception.BadRequestException;
import com.bdms.repository.DonorRepository;
import com.bdms.repository.ReportRepository;
import com.bdms.service.CurrentUserService;
import com.bdms.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final DonorRepository donorRepository;
    private final CurrentUserService currentUserService;

    @Value("${storage.reports.location:uploads/reports}")
    private String reportStoragePath;

    @Override
    public ReportResponse upload(MultipartFile file, Long userId) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        Long resolvedUserId = resolveTargetUserId(userId);
        if (!donorRepository.existsById(resolvedUserId)) {
            throw new BadRequestException("Invalid user id: " + resolvedUserId);
        }

        try {
            Path folder = Path.of(reportStoragePath);
            Files.createDirectories(folder);

            String extension = extractExtension(file.getOriginalFilename());
            String storedName = UUID.randomUUID() + extension;
            Path destination = folder.resolve(storedName);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/reports/" + storedName;
            Report saved = reportRepository.save(Report.builder()
                    .userId(resolvedUserId)
                    .fileUrl(fileUrl)
                    .build());

            return toResponse(saved);
        } catch (IOException ex) {
            throw new BadRequestException("Unable to upload report file");
        }
    }

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
    public List<ReportResponse> list(Long userId) {
        Long resolvedUserId = userId != null ? resolveTargetUserId(userId) : currentUserService.getCurrentUser().getId();
        return getByUserId(resolvedUserId);
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

    private Long resolveTargetUserId(Long userId) {
        Donor currentUser = currentUserService.getCurrentUser();
        if (userId == null) {
            return currentUser.getId();
        }

        if (userId.equals(currentUser.getId())
                || currentUser.getRole() == Role.ROLE_ADMIN
                || currentUser.getRole() == Role.ROLE_SUPER_ADMIN
                || currentUser.getRole() == Role.ROLE_HOSPITAL) {
            return userId;
        }

        throw new BadRequestException("You are not allowed to upload/list reports for another user");
    }

    private String extractExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int index = filename.lastIndexOf('.');
        return index < 0 ? "" : filename.substring(index);
    }
}
