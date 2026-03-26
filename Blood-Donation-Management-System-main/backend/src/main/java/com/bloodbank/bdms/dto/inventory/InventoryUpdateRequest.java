package com.bloodbank.bdms.dto.inventory;

import com.bloodbank.bdms.entity.enums.BloodGroup;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryUpdateRequest {
  @NotNull
  private BloodGroup bloodGroup;

  private int unitsDelta;

  private String reason;
}
