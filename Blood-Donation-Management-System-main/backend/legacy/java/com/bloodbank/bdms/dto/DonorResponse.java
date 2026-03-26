package com.bloodbank.bdms.dto;

public record DonorResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String phone,
        String city,
        String state,
        String address,
        String gender,
        String bloodGroup,
        String dateOfBirth,
        boolean readyToDonate,
        String imageUrl
) {
}
