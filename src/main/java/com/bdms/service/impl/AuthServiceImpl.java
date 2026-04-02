package com.bdms.service.impl;

import com.bdms.dto.auth.AuthResponse;
import com.bdms.dto.auth.LoginRequest;
import com.bdms.dto.auth.RefreshTokenRequest;
import com.bdms.dto.auth.RegisterRequest;
import com.bdms.dto.donor.DonorResponse;
import com.bdms.entity.Donor;
import com.bdms.entity.Hospital;
import com.bdms.entity.RefreshToken;
import com.bdms.entity.Role;
import com.bdms.entity.SubscriptionPlan;
import com.bdms.exception.BadRequestException;
import com.bdms.repository.DonorRepository;
import com.bdms.repository.HospitalRepository;
import com.bdms.security.JwtService;
import com.bdms.service.AuditLogService;
import com.bdms.service.AuthService;
import com.bdms.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final DonorRepository donorRepository;
    private final HospitalRepository hospitalRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final AuditLogService auditLogService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (donorRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        Role resolvedRole = resolveRole(request.getRole());
        Hospital hospital = resolveRegistrationHospital(request.getHospitalId());

        Donor donor = Donor.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .bloodGroup(request.getBloodGroup())
                .location(request.getLocation())
                .phoneNumber(request.getPhoneNumber())
                .availabilityStatus(request.getAvailabilityStatus())
                .role(resolvedRole)
                .hospital(hospital)
                .build();

        Donor savedDonor = donorRepository.save(donor);
            String accessToken = jwtService.generateAccessToken(savedDonor);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedDonor);
        log.info("User registered successfully with email={}", savedDonor.getEmail());
            auditLogService.log("REGISTER", "DONOR", savedDonor.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(toDonorResponse(savedDonor))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Donor donor = donorRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        String accessToken = jwtService.generateAccessToken(donor);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(donor);
        log.info("User logged in successfully with email={}", donor.getEmail());
        auditLogService.log("LOGIN", "DONOR", donor.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(toDonorResponse(donor))
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.validate(request.getRefreshToken());
        Donor donor = refreshToken.getUser();

        String accessToken = jwtService.generateAccessToken(donor);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .user(toDonorResponse(donor))
                .build();
    }

    private Role resolveRole(String role) {
        if (role == null) {
            return Role.ROLE_DONOR;
        }

        return switch (role.toUpperCase()) {
            case "ROLE_SUPER_ADMIN", "SUPER_ADMIN" -> Role.ROLE_SUPER_ADMIN;
            case "ROLE_ADMIN", "ADMIN" -> Role.ROLE_ADMIN;
            case "ROLE_HOSPITAL", "HOSPITAL" -> Role.ROLE_HOSPITAL;
            case "ROLE_DONOR", "ROLE_USER", "USER", "DONOR" -> Role.ROLE_DONOR;
            default -> throw new BadRequestException("Invalid role. Use SUPER_ADMIN, ADMIN, HOSPITAL, or DONOR");
        };
    }

    private Hospital resolveRegistrationHospital(Long requestedHospitalId) {
        if (requestedHospitalId != null) {
            return hospitalRepository.findById(requestedHospitalId)
                .orElseThrow(() -> new BadRequestException("Hospital not found with id: " + requestedHospitalId));
        }

        return hospitalRepository.findAll().stream().findFirst().orElseGet(() ->
            hospitalRepository.save(Hospital.builder()
                .name("BDMS Default Hospital")
                .location("Default City")
                .email("default-hospital@bdms.local")
                .subscriptionPlan(SubscriptionPlan.FREE)
                .build())
        );
    }

    private DonorResponse toDonorResponse(Donor donor) {
        return DonorResponse.builder()
                .id(donor.getId())
                .name(donor.getName())
                .email(donor.getEmail())
                .bloodGroup(donor.getBloodGroup())
                .location(donor.getLocation())
                .phoneNumber(donor.getPhoneNumber())
                .availabilityStatus(donor.getAvailabilityStatus())
                .hospitalId(donor.getHospital() == null ? null : donor.getHospital().getId())
                .hospitalName(donor.getHospital() == null ? null : donor.getHospital().getName())
                .lastDonationDate(donor.getLastDonationDate())
                .totalDonations(donor.getTotalDonations())
                .role(donor.getRole())
                .build();
    }
}
