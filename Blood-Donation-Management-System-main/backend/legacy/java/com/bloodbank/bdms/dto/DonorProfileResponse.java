package com.bloodbank.bdms.dto;

public record DonorProfileResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
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
