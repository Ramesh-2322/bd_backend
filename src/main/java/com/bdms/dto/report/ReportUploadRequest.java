package com.bdms.dto.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportUploadRequest {

    @NotNull(message = "User id is required")
    private Long userId;

    @NotBlank(message = "File URL is required")
    @Size(max = 600, message = "File URL must be at most 600 characters")
    @Pattern(regexp = "^(https?://).+", message = "File URL must start with http:// or https://")
    private String fileUrl;
}
