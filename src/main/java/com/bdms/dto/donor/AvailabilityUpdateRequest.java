package com.bdms.dto.donor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailabilityUpdateRequest {

    @NotNull(message = "Availability is required")
    private Boolean available;
}