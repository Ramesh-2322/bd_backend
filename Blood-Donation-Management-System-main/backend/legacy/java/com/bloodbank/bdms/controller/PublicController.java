package com.bloodbank.bdms.controller;

import com.bloodbank.bdms.dto.BloodGroupResponse;
import com.bloodbank.bdms.dto.BloodRequestCreate;
import com.bloodbank.bdms.dto.BloodRequestResponse;
import com.bloodbank.bdms.dto.DonorResponse;
import com.bloodbank.bdms.entity.DonorProfile;
import com.bloodbank.bdms.service.PublicService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    private final PublicService publicService;

    public PublicController(PublicService publicService) {
        this.publicService = publicService;
    }

    @GetMapping("/blood-groups")
    public List<BloodGroupResponse> bloodGroups() {
        return publicService.getBloodGroups();
    }

    @GetMapping("/donors")
    public List<DonorResponse> donors(@RequestParam(required = false) Long bloodGroupId,
                                      @RequestParam(defaultValue = "true") boolean readyOnly) {
        List<DonorProfile> donors = publicService.getDonors(bloodGroupId, readyOnly);
        return donors.stream().map(publicService.getDonorService()::toDonorResponse).collect(Collectors.toList());
    }

    @GetMapping("/donors/{id}")
    public DonorResponse donorDetails(@PathVariable Long id) {
        DonorProfile donor = publicService.getDonorById(id);
        return publicService.getDonorService().toDonorResponse(donor);
    }

    @PostMapping("/requests")
    public BloodRequestResponse createRequest(@RequestBody BloodRequestCreate request) {
        return publicService.createRequest(request);
    }

    @GetMapping("/requests")
    public List<BloodRequestResponse> getRequests() {
        return publicService.getRequests();
    }
}
