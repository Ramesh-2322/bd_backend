package com.bdms.service.impl;

import com.bdms.entity.Donor;
import com.bdms.entity.Role;
import com.bdms.exception.ResourceNotFoundException;
import com.bdms.repository.DonorRepository;
import com.bdms.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {

    private final DonorRepository donorRepository;

    @Override
    public Donor getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationCredentialsNotFoundException("Authentication required");
        }

        String username = authentication.getName();
        if (username == null || username.isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("Invalid authentication context");
        }

        return donorRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    @Override
    public boolean isSuperAdmin() {
        return getCurrentUser().getRole() == Role.ROLE_SUPER_ADMIN;
    }

    @Override
    public Long getCurrentHospitalId() {
        Donor donor = getCurrentUser();
        return donor.getHospital() == null ? null : donor.getHospital().getId();
    }
}
