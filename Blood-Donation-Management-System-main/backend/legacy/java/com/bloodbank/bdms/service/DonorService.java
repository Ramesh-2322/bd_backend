package com.bloodbank.bdms.service;

import com.bloodbank.bdms.dto.DonorProfileResponse;
import com.bloodbank.bdms.dto.DonorResponse;
import com.bloodbank.bdms.dto.UpdateProfileRequest;
import com.bloodbank.bdms.entity.AppUser;
import com.bloodbank.bdms.entity.DonorProfile;
import com.bloodbank.bdms.repository.AppUserRepository;
import com.bloodbank.bdms.repository.DonorProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class DonorService {
    private final AppUserRepository userRepository;
    private final DonorProfileRepository donorRepository;
    private final FileStorageService fileStorageService;

    public DonorService(AppUserRepository userRepository,
                        DonorProfileRepository donorRepository,
                        FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.donorRepository = donorRepository;
        this.fileStorageService = fileStorageService;
    }

    public DonorProfileResponse getProfile(String username) {
        AppUser user = userRepository.findByUsername(username).orElseThrow();
        DonorProfile profile = donorRepository.findByUserId(user.getId()).orElseThrow();
        return toProfileResponse(profile);
    }

    public DonorProfileResponse updateProfile(String username, UpdateProfileRequest request) {
        AppUser user = userRepository.findByUsername(username).orElseThrow();
        DonorProfile profile = donorRepository.findByUserId(user.getId()).orElseThrow();

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            profile.setPhone(request.getPhone());
        }
        if (request.getState() != null && !request.getState().isBlank()) {
            profile.setState(request.getState());
        }
        if (request.getCity() != null && !request.getCity().isBlank()) {
            profile.setCity(request.getCity());
        }
        if (request.getAddress() != null && !request.getAddress().isBlank()) {
            profile.setAddress(request.getAddress());
        }
        String imagePath = fileStorageService.store(request.getImage());
        if (imagePath != null) {
            profile.setImagePath(imagePath);
        }

        userRepository.save(user);
        donorRepository.save(profile);
        return toProfileResponse(profile);
    }

    public DonorProfileResponse toggleReady(String username) {
        AppUser user = userRepository.findByUsername(username).orElseThrow();
        DonorProfile profile = donorRepository.findByUserId(user.getId()).orElseThrow();
        profile.setReadyToDonate(!profile.isReadyToDonate());
        donorRepository.save(profile);
        return toProfileResponse(profile);
    }

    public DonorResponse toDonorResponse(DonorProfile profile) {
        AppUser user = profile.getUser();
        String fullName = (user.getFirstName() == null ? "" : user.getFirstName()) +
                " " + (user.getLastName() == null ? "" : user.getLastName());
        return new DonorResponse(
                profile.getId(),
                user.getUsername(),
                fullName.trim(),
                user.getEmail(),
                profile.getPhone(),
                profile.getCity(),
                profile.getState(),
                profile.getAddress(),
                profile.getGender(),
                profile.getBloodGroup().getName(),
                profile.getDateOfBirth(),
                profile.isReadyToDonate(),
                profile.getImagePath()
        );
    }

    public DonorProfileResponse toProfileResponse(DonorProfile profile) {
        AppUser user = profile.getUser();
        return new DonorProfileResponse(
                profile.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                profile.getPhone(),
                profile.getCity(),
                profile.getState(),
                profile.getAddress(),
                profile.getGender(),
                profile.getBloodGroup().getName(),
                profile.getDateOfBirth(),
                profile.isReadyToDonate(),
                profile.getImagePath()
        );
    }
}
