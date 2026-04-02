package com.bdms.dto.subscription;

import com.bdms.entity.SubscriptionPlan;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private Long id;
    private Long hospitalId;
    private String hospitalName;
    private SubscriptionPlan planType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;
}
