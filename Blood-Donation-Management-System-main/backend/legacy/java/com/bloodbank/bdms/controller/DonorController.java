package com.bloodbank.bdms.controller;

import com.bloodbank.bdms.dto.DonorProfileResponse;
import com.bloodbank.bdms.dto.UpdateProfileRequest;
import com.bloodbank.bdms.service.DonorService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/profile")
public class DonorController {
    private final DonorService donorService;

    public DonorController(DonorService donorService) {
        this.donorService = donorService;
    }

    @GetMapping
    public DonorProfileResponse getProfile(Principal principal) {
        return donorService.getProfile(principal.getName());
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DonorProfileResponse updateProfile(@ModelAttribute UpdateProfileRequest request, Principal principal) {
        return donorService.updateProfile(principal.getName(), request);
    }

    @PostMapping("/toggle-ready")
    public DonorProfileResponse toggleReady(Principal principal) {
        return donorService.toggleReady(principal.getName());
    }
}
