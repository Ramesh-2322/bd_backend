package com.bloodbank.bdms.controller;

import com.bloodbank.bdms.dto.inventory.InventoryResponse;
import com.bloodbank.bdms.dto.inventory.InventoryUpdateRequest;
import com.bloodbank.bdms.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
  private final InventoryService inventoryService;

  public InventoryController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public List<InventoryResponse> listInventory() {
    return inventoryService.listInventory();
  }

  @PostMapping("/adjust")
  @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
  public InventoryResponse adjust(@Valid @RequestBody InventoryUpdateRequest request) {
    return inventoryService.adjustInventory(request);
  }
}
