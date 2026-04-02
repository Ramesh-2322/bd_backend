package com.bdms.dto.hospital;

import com.bdms.entity.SubscriptionPlan;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalResponse {

    private Long id;
    private String name;
    private String location;
    private String email;
    private SubscriptionPlan subscriptionPlan;
}
