package com.bloodbank.bdms.controller;

import com.bloodbank.bdms.dto.user.UserCreateRequest;
import com.bloodbank.bdms.dto.user.UserResponse;
import com.bloodbank.bdms.entity.enums.UserStatus;
import com.bloodbank.bdms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public List<UserResponse> listUsers() {
    return userService.listUsers();
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
    return userService.createStaff(request);
  }

  @PatchMapping("/{id}/status")
  @PreAuthorize("hasRole('ADMIN')")
  public UserResponse updateStatus(@PathVariable Long id, @RequestParam UserStatus status) {
    return userService.updateStatus(id, status);
  }
}
