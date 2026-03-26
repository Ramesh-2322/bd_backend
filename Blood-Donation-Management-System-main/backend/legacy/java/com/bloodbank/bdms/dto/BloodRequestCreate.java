package com.bloodbank.bdms.dto;

public record BloodRequestCreate(
        String name,
        String email,
        String phone,
        String state,
        String city,
        String address,
        String bloodGroup,
        String requestDate
) {
}
