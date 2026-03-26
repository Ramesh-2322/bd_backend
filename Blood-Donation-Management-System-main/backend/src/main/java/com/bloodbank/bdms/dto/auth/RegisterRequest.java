package com.bloodbank.bdms.dto.auth;

import com.bloodbank.bdms.entity.enums.BloodGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterRequest {
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
  private BloodGroup bloodGroup;

  @NotBlank
  private String gender;

  private LocalDate dateOfBirth;

  private String address;

  private String city;
}
