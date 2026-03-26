package com.bloodbank.bdms.service;

import com.bloodbank.bdms.config.JwtUtil;
import com.bloodbank.bdms.dto.auth.AuthResponse;
import com.bloodbank.bdms.dto.auth.LoginRequest;
import com.bloodbank.bdms.dto.auth.RegisterRequest;
import com.bloodbank.bdms.dto.user.UserResponse;
import com.bloodbank.bdms.entity.DonorProfile;
import com.bloodbank.bdms.entity.Role;
import com.bloodbank.bdms.entity.User;
import com.bloodbank.bdms.entity.enums.RoleName;
import com.bloodbank.bdms.entity.enums.UserStatus;
import com.bloodbank.bdms.exception.ApiException;
import com.bloodbank.bdms.repository.DonorProfileRepository;
import com.bloodbank.bdms.repository.RoleRepository;
import com.bloodbank.bdms.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final DonorProfileRepository donorProfileRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

  public AuthService(
      UserRepository userRepository,
      RoleRepository roleRepository,
      DonorProfileRepository donorProfileRepository,
      PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager,
      JwtUtil jwtUtil
  ) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.donorProfileRepository = donorProfileRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtUtil = jwtUtil;
  }

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new ApiException("Email already registered");
    }

    Role donorRole = roleRepository.findByName(RoleName.ROLE_DONOR)
        .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_DONOR).build()));

    User user = User.builder()
        .name(request.getName())
        .email(request.getEmail())
        .phone(request.getPhone())
        .passwordHash(passwordEncoder.encode(request.getPassword()))
        .status(UserStatus.ACTIVE)
        .build();
    user.getRoles().add(donorRole);
    user = userRepository.save(user);

    DonorProfile profile = DonorProfile.builder()
        .user(user)
        .bloodGroup(request.getBloodGroup())
        .gender(request.getGender())
        .dateOfBirth(request.getDateOfBirth())
        .address(request.getAddress())
        .city(request.getCity())
        .available(true)
        .totalDonations(0)
        .build();
    donorProfileRepository.save(profile);

    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    String token = jwtUtil.generateToken((UserDetails) auth.getPrincipal());
    return new AuthResponse(token, toUserResponse(user));
  }

  public AuthResponse login(LoginRequest request) {
    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new ApiException("User not found"));

    String token = jwtUtil.generateToken((UserDetails) auth.getPrincipal());
    return new AuthResponse(token, toUserResponse(user));
  }

  private UserResponse toUserResponse(User user) {
    Set<String> roles = user.getRoles().stream()
        .map(role -> role.getName().name())
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
