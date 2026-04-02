package com.bdms.dto.file;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalReportResponse {

    private Long id;
    private Long donorId;
    private String originalFileName;
    private String storedFilePath;
    private String contentType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
}
