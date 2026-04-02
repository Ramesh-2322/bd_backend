package com.bdms.dto.hospital;

import com.bdms.entity.SubscriptionPlan;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HospitalRequest {

    @NotBlank(message = "Hospital name is required")
    private String name;

    @NotBlank(message = "Hospital location is required")
    private String location;

    @NotBlank(message = "Hospital email is required")
    @Email(message = "Invalid hospital email format")
    private String email;

    @NotNull(message = "Subscription plan is required")
    private SubscriptionPlan subscriptionPlan;
}
