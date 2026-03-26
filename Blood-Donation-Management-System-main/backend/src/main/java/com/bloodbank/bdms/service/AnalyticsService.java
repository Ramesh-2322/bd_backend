package com.bloodbank.bdms.service;

import com.bloodbank.bdms.dto.analytics.AnalyticsSummaryResponse;
import com.bloodbank.bdms.dto.inventory.InventoryResponse;
import com.bloodbank.bdms.entity.enums.RequestStatus;
import com.bloodbank.bdms.repository.AppointmentRepository;
import com.bloodbank.bdms.repository.BloodRequestRepository;
import com.bloodbank.bdms.repository.DonationRepository;
import com.bloodbank.bdms.repository.DonorProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {
  private final DonorProfileRepository donorRepository;
  private final DonationRepository donationRepository;
  private final BloodRequestRepository requestRepository;
  private final AppointmentRepository appointmentRepository;
  private final InventoryService inventoryService;

  public AnalyticsService(
      DonorProfileRepository donorRepository,
      DonationRepository donationRepository,
      BloodRequestRepository requestRepository,
      AppointmentRepository appointmentRepository,
      InventoryService inventoryService
  ) {
    this.donorRepository = donorRepository;
    this.donationRepository = donationRepository;
    this.requestRepository = requestRepository;
    this.appointmentRepository = appointmentRepository;
    this.inventoryService = inventoryService;
  }

  public AnalyticsSummaryResponse summary() {
    List<InventoryResponse> inventory = inventoryService.listInventory();
    return AnalyticsSummaryResponse.builder()
        .totalDonors(donorRepository.count())
        .totalDonations(donationRepository.count())
        .totalRequests(requestRepository.count())
        .openRequests(requestRepository.findByStatus(RequestStatus.OPEN).size())
        .totalAppointments(appointmentRepository.count())
        .inventory(inventory)
        .build();
  }
}
