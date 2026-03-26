package com.bloodbank.bdms.controller;

import com.bloodbank.bdms.dto.AuthRequest;
import com.bloodbank.bdms.dto.AuthResponse;
import com.bloodbank.bdms.dto.RegisterRequest;
import com.bloodbank.bdms.entity.AppUser;
import com.bloodbank.bdms.entity.BloodGroup;
import com.bloodbank.bdms.entity.DonorProfile;
import com.bloodbank.bdms.entity.Role;
import com.bloodbank.bdms.repository.AppUserRepository;
import com.bloodbank.bdms.repository.BloodGroupRepository;
import com.bloodbank.bdms.repository.DonorProfileRepository;
import com.bloodbank.bdms.service.FileStorageService;
import com.bloodbank.bdms.util.JwtUtil;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AppUserRepository userRepository;
    private final BloodGroupRepository bloodGroupRepository;
    private final DonorProfileRepository donorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final FileStorageService fileStorageService;

    public AuthController(AppUserRepository userRepository,
                          BloodGroupRepository bloodGroupRepository,
                          DonorProfileRepository donorRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.bloodGroupRepository = bloodGroupRepository;
        this.donorRepository = donorRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AuthResponse register(@ModelAttribute RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        BloodGroup group = bloodGroupRepository.findByName(request.getBloodGroup())
                .orElseThrow(() -> new IllegalArgumentException("Blood group not found"));

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(Role.DONOR);
        userRepository.save(user);

        DonorProfile profile = new DonorProfile();
        profile.setUser(user);
        profile.setPhone(request.getPhone());
        profile.setState(request.getState());
        profile.setCity(request.getCity());
        profile.setAddress(request.getAddress());
        profile.setGender(request.getGender());
        profile.setBloodGroup(group);
        profile.setDateOfBirth(request.getDateOfBirth());
        String imagePath = fileStorageService.store(request.getImage());
        profile.setImagePath(imagePath);
        donorRepository.save(profile);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        Set<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        String token = jwtUtil.generateToken(request.getUsername(), new HashMap<>());
        return new AuthResponse(token, request.getUsername(), roles);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        Set<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        String token = jwtUtil.generateToken(request.username(), new HashMap<>());
        return new AuthResponse(token, request.username(), roles);
    }
}
