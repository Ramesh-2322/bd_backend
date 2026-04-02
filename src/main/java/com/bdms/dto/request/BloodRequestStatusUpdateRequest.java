package com.bdms.dto.request;

import com.bdms.entity.RequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BloodRequestStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private RequestStatus status;
}
