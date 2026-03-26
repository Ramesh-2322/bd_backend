package com.bloodbank.bdms.controller;

import com.bloodbank.bdms.dto.analytics.AnalyticsSummaryResponse;
import com.bloodbank.bdms.service.AnalyticsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
  private final AnalyticsService analyticsService;

  public AnalyticsController(AnalyticsService analyticsService) {
    this.analyticsService = analyticsService;
  }

  @GetMapping("/summary")
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public AnalyticsSummaryResponse summary() {
    return analyticsService.summary();
  }
}
