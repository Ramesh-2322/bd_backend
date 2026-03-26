package com.bloodbank.bdms.dto.user;

import com.bloodbank.bdms.entity.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {
  @NotBlank
  private String name;

  @Email
  @NotBlank
  private String email;

  @NotBlank
  private String phone;

  @NotBlank
  private String password;

  @NotNull
  private RoleName role;
}
