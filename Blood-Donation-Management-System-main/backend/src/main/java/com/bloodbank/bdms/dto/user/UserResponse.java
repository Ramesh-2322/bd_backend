package com.bloodbank.bdms.dto.user;

import com.bloodbank.bdms.entity.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class UserResponse {
  private Long id;
  private String name;
  private String email;
  private String phone;
  private Set<String> roles;
  private UserStatus status;
}
