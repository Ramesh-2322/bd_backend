package com.bdms.controller;

import com.bdms.common.payload.ApiResponse;
import com.bdms.dto.subscription.SubscriptionRequest;
import com.bdms.dto.subscription.SubscriptionResponse;
import com.bdms.entity.SubscriptionPlan;
import com.bdms.service.CurrentUserService;
import com.bdms.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final CurrentUserService currentUserService;

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAvailablePlans() {
        List<Map<String, Object>> plans = List.of(
                Map.of("code", "FREE", "name", "FREE", "price", "$0/mo"),
                Map.of("code", "PREMIUM", "name", "PREMIUM", "price", "$49/mo"),
                Map.of("code", "ENTERPRISE", "name", "ENTERPRISE", "price", "$199/mo")
        );
        return ResponseEntity.ok(ApiResponse.success("Subscription plans fetched successfully", plans));
    }

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<Map<String, String>>> getCurrentSubscription() {
        SubscriptionPlan plan = SubscriptionPlan.FREE;
        if (currentUserService.getCurrentUser().getHospital() != null
                && currentUserService.getCurrentUser().getHospital().getSubscriptionPlan() != null) {
            plan = currentUserService.getCurrentUser().getHospital().getSubscriptionPlan();
        }
        return ResponseEntity.ok(ApiResponse.success("Current subscription fetched successfully",
                Map.of("planCode", plan.name())));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> createOrUpdate(@Valid @RequestBody SubscriptionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Subscription updated successfully", subscriptionService.createOrUpdate(request)));
    }

    @GetMapping("/hospital/{hospitalId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getByHospital(@PathVariable Long hospitalId) {
        if (!currentUserService.isSuperAdmin() && !hospitalId.equals(currentUserService.getCurrentHospitalId())) {
            return ResponseEntity.status(403).body(ApiResponse.error("Access denied", null));
        }
        return ResponseEntity.ok(ApiResponse.success("Subscriptions fetched successfully", subscriptionService.getByHospital(hospitalId)));
    }
}
