package com.bdms.dto.subscription;

import com.bdms.entity.SubscriptionPlan;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SubscriptionRequest {

    @NotNull(message = "Hospital id is required")
    private Long hospitalId;

    @NotNull(message = "Plan type is required")
    private SubscriptionPlan planType;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Active flag is required")
    private Boolean active;
}
