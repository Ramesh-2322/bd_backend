package com.bdms.dto.request;

import com.bdms.entity.UrgencyLevel;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BloodRequestCreateRequest {

    @NotBlank(message = "Patient name is required")
    @Size(max = 100, message = "Patient name must be at most 100 characters")
    private String patientName;

    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Blood group must be one of A+, A-, B+, B-, AB+, AB-, O+, O-")
    private String bloodGroup;

    @NotNull(message = "Units required is required")
    @Min(value = 1, message = "Units required must be at least 1")
    @Max(value = 10, message = "Units required cannot exceed 10")
    private Integer unitsRequired;

    @NotBlank(message = "Hospital name is required")
    @Size(max = 150, message = "Hospital name must be at most 150 characters")
    private String hospitalName;

    @NotBlank(message = "Location is required")
    @Size(max = 120, message = "Location must be at most 120 characters")
    private String location;

    @NotNull(message = "Urgency level is required")
    private UrgencyLevel urgencyLevel;
}
