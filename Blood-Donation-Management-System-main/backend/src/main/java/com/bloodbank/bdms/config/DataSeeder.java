package com.bloodbank.bdms.config;

import com.bloodbank.bdms.entity.Role;
import com.bloodbank.bdms.entity.User;
import com.bloodbank.bdms.entity.enums.RoleName;
import com.bloodbank.bdms.entity.enums.UserStatus;
import com.bloodbank.bdms.repository.RoleRepository;
import com.bloodbank.bdms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.admin.email}")
  private String adminEmail;

  @Value("${app.admin.password}")
  private String adminPassword;

  @Value("${app.admin.name}")
  private String adminName;

  @Value("${app.admin.phone}")
  private String adminPhone;

  public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.roleRepository = roleRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) {
    Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
        .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_ADMIN).build()));
    roleRepository.findByName(RoleName.ROLE_STAFF)
        .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_STAFF).build()));
    roleRepository.findByName(RoleName.ROLE_DONOR)
        .orElseGet(() -> roleRepository.save(Role.builder().name(RoleName.ROLE_DONOR).build()));

    if (!userRepository.existsByEmail(adminEmail)) {
      User admin = User.builder()
          .name(adminName)
          .email(adminEmail)
          .phone(adminPhone)
          .passwordHash(passwordEncoder.encode(adminPassword))
          .status(UserStatus.ACTIVE)
          .build();
      admin.getRoles().add(adminRole);
      userRepository.save(admin);
    }
  }
}
