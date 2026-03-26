package com.bloodbank.bdms.repository;

import com.bloodbank.bdms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bloodbank.bdms.entity.enums.RoleName;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
  boolean existsByEmail(String email);
  List<User> findByRolesName(RoleName name);
}
