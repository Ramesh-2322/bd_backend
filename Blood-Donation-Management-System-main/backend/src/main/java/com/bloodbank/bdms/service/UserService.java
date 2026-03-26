package com.bloodbank.bdms.service;

import com.bloodbank.bdms.dto.user.UserCreateRequest;
import com.bloodbank.bdms.dto.user.UserResponse;
import com.bloodbank.bdms.entity.Role;
import com.bloodbank.bdms.entity.User;
import com.bloodbank.bdms.entity.enums.UserStatus;
import com.bloodbank.bdms.exception.ApiException;
import com.bloodbank.bdms.exception.NotFoundException;
import com.bloodbank.bdms.repository.RoleRepository;
import com.bloodbank.bdms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuditService auditService;

  public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                     AuditService auditService) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
    this.auditService = auditService;
  }

  public List<UserResponse> listUsers() {
    return userRepository.findAll().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public UserResponse createStaff(UserCreateRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new ApiException("Email already registered");
    }

    Role role = roleRepository.findByName(request.getRole())
        .orElseGet(() -> roleRepository.save(Role.builder().name(request.getRole()).build()));

    User user = User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .phone(request.getPhone())
        .passwordHash(passwordEncoder.encode(request.getPassword()))
        .status(UserStatus.ACTIVE)
        .build();
    user.getRoles().add(role);

    User saved = userRepository.save(user);
    auditService.record("CREATE_USER", "User", saved.getId(), "role=" + request.getRole());
    return toResponse(saved);
  }

  @Transactional
  public UserResponse updateStatus(Long userId, UserStatus status) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("User not found"));
    user.setStatus(status);
    auditService.record("UPDATE_USER_STATUS", "User", user.getId(), "status=" + status);
    return toResponse(user);
  }

  private UserResponse toResponse(User user) {
    Set<String> roles = user.getRoles().stream()
        .map(r -> r.getName().name())
        .collect(Collectors.toSet());
    return UserResponse.builder()
        .id(user.getId())
        .name(user.getName())
        .email(user.getEmail())
        .phone(user.getPhone())
        .roles(roles)
        .status(user.getStatus())
        .build();
  }
}
