package com.bdms.service.impl;

import com.bdms.dto.file.MedicalReportResponse;
import com.bdms.entity.Donor;
import com.bdms.entity.MedicalReport;
import com.bdms.exception.BadRequestException;
import com.bdms.repository.MedicalReportRepository;
import com.bdms.service.CurrentUserService;
import com.bdms.service.MedicalReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MedicalReportServiceImpl implements MedicalReportService {

    private final MedicalReportRepository medicalReportRepository;
    private final CurrentUserService currentUserService;

    @Value("${storage.medical-reports.location:uploads/medical-reports}")
    private String storagePath;

    @Override
    public MedicalReportResponse upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }

        Donor donor = currentUserService.getCurrentUser();

        try {
            Path folder = Path.of(storagePath);
            Files.createDirectories(folder);

            String extension = extractExtension(file.getOriginalFilename());
            String storedName = UUID.randomUUID() + extension;
            Path destination = folder.resolve(storedName);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            MedicalReport saved = medicalReportRepository.save(MedicalReport.builder()
                    .donor(donor)
                    .originalFileName(file.getOriginalFilename() == null ? storedName : file.getOriginalFilename())
                    .storedFilePath(destination.toString())
                    .contentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType())
                    .fileSize(file.getSize())
                    .build());

            return toResponse(saved);
        } catch (IOException ex) {
            throw new BadRequestException("Unable to upload file");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalReportResponse> getMyReports(int page, int size, String sortBy, String sortDir) {
        Donor donor = currentUserService.getCurrentUser();
        Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return medicalReportRepository.findByDonorId(donor.getId(), pageable)
                .map(this::toResponse);
    }

    private String extractExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int index = filename.lastIndexOf('.');
        return index < 0 ? "" : filename.substring(index);
    }

    private MedicalReportResponse toResponse(MedicalReport report) {
        return MedicalReportResponse.builder()
                .id(report.getId())
                .donorId(report.getDonor().getId())
                .originalFileName(report.getOriginalFileName())
                .storedFilePath(report.getStoredFilePath())
                .contentType(report.getContentType())
                .fileSize(report.getFileSize())
                .uploadedAt(report.getUploadedAt())
                .build();
    }
}
