package com.bloodbank.bdms.service;

import com.bloodbank.bdms.dto.inventory.InventoryResponse;
import com.bloodbank.bdms.dto.inventory.InventoryUpdateRequest;
import com.bloodbank.bdms.entity.BloodInventory;
import com.bloodbank.bdms.entity.enums.BloodGroup;
import com.bloodbank.bdms.exception.NotFoundException;
import com.bloodbank.bdms.repository.BloodInventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {
  private final BloodInventoryRepository inventoryRepository;
  private final AuditService auditService;

  public InventoryService(BloodInventoryRepository inventoryRepository, AuditService auditService) {
    this.inventoryRepository = inventoryRepository;
    this.auditService = auditService;
  }

  public List<InventoryResponse> listInventory() {
    ensureAllGroups();
    return inventoryRepository.findAll().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public InventoryResponse adjustInventory(InventoryUpdateRequest request) {
    BloodInventory inventory = inventoryRepository.findByBloodGroup(request.getBloodGroup())
        .orElseGet(() -> inventoryRepository.save(BloodInventory.builder()
            .bloodGroup(request.getBloodGroup())
            .unitsAvailable(0)
            .build()));

    int updated = inventory.getUnitsAvailable() + request.getUnitsDelta();
    inventory.setUnitsAvailable(Math.max(updated, 0));
    auditService.record("ADJUST_INVENTORY", "BloodInventory", inventory.getId(),
        "delta=" + request.getUnitsDelta());
    return toResponse(inventory);
  }

  @Transactional
  public void addDonation(BloodGroup group, int quantityMl) {
    int units = Math.max(1, quantityMl / 350);
    InventoryUpdateRequest request = new InventoryUpdateRequest();
    request.setBloodGroup(group);
    request.setUnitsDelta(units);
    adjustInventory(request);
  }

  @Transactional
  public void consumeUnits(BloodGroup group, int units) {
    BloodInventory inventory = inventoryRepository.findByBloodGroup(group)
        .orElseThrow(() -> new NotFoundException("Inventory not found for blood group"));
    int updated = inventory.getUnitsAvailable() - units;
    inventory.setUnitsAvailable(Math.max(updated, 0));
    auditService.record("CONSUME_INVENTORY", "BloodInventory", inventory.getId(), "units=" + units);
  }

  private InventoryResponse toResponse(BloodInventory inventory) {
    return InventoryResponse.builder()
        .bloodGroup(inventory.getBloodGroup())
        .unitsAvailable(inventory.getUnitsAvailable())
        .updatedAt(inventory.getUpdatedAt())
        .build();
  }

  private void ensureAllGroups() {
    Arrays.stream(BloodGroup.values()).forEach(group -> {
      inventoryRepository.findByBloodGroup(group).orElseGet(() ->
          inventoryRepository.save(BloodInventory.builder()
              .bloodGroup(group)
              .unitsAvailable(0)
              .build()));
    });
  }
}
