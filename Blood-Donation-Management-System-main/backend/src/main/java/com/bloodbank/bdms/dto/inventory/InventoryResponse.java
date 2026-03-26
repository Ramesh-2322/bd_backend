package com.bloodbank.bdms.dto.inventory;

import com.bloodbank.bdms.entity.enums.BloodGroup;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class InventoryResponse {
  private BloodGroup bloodGroup;
  private int unitsAvailable;
  private Instant updatedAt;
}
