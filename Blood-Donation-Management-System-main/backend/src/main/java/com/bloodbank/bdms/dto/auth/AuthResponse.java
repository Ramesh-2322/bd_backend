package com.bloodbank.bdms.dto.auth;

import com.bloodbank.bdms.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
  private String token;
  private UserResponse user;
}
