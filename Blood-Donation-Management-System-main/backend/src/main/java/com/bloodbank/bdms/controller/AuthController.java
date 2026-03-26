package com.bloodbank.bdms.controller;

import com.bloodbank.bdms.dto.auth.AuthResponse;
import com.bloodbank.bdms.dto.auth.LoginRequest;
import com.bloodbank.bdms.dto.auth.RegisterRequest;
import com.bloodbank.bdms.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    return authService.register(request);
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request);
  }
}
