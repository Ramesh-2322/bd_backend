package com.bloodbank.bdms.controller;

import com.bloodbank.bdms.dto.request.BloodRequestCreateRequest;
import com.bloodbank.bdms.dto.request.BloodRequestResponse;
import com.bloodbank.bdms.dto.request.BloodRequestUpdateRequest;
import com.bloodbank.bdms.service.RequestService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {
  private final RequestService requestService;

  public RequestController(RequestService requestService) {
    this.requestService = requestService;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public List<BloodRequestResponse> listRequests() {
    return requestService.listRequests();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public BloodRequestResponse getRequest(@PathVariable Long id) {
    return requestService.getRequest(id);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public BloodRequestResponse createRequest(@Valid @RequestBody BloodRequestCreateRequest request) {
    return requestService.createRequest(request);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public BloodRequestResponse updateRequest(@PathVariable Long id, @RequestBody BloodRequestUpdateRequest request) {
    return requestService.updateRequest(id, request);
  }
}
