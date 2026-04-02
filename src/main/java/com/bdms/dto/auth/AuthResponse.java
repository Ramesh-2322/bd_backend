package com.bdms.dto.auth;

import com.bdms.dto.donor.DonorResponse;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private DonorResponse user;
}
