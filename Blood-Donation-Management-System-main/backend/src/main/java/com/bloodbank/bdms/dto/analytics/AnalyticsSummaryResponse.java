package com.bloodbank.bdms.dto.analytics;

import com.bloodbank.bdms.dto.inventory.InventoryResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AnalyticsSummaryResponse {
  private long totalDonors;
  private long totalDonations;
  private long totalRequests;
  private long openRequests;
  private long totalAppointments;
  private List<InventoryResponse> inventory;
}
