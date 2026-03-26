package com.bloodbank.bdms.entity;

import com.bloodbank.bdms.entity.enums.BloodGroup;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "blood_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodInventory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, unique = true)
  private BloodGroup bloodGroup;

  @Column(nullable = false)
  private int unitsAvailable;

  @UpdateTimestamp
  private Instant updatedAt;
}
