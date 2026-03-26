package com.bloodbank.bdms.dto;

public record BloodRequestResponse(
        Long id,
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
