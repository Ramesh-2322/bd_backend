package com.bloodbank.bdms.entity;

import com.bloodbank.bdms.entity.enums.BloodGroup;
import com.bloodbank.bdms.entity.enums.RequestPriority;
import com.bloodbank.bdms.entity.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "blood_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String requesterName;

  @Column(nullable = false)
  private String requesterContact;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BloodGroup bloodGroup;

  @Column(nullable = false)
  private int unitsNeeded;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RequestPriority priority;

  private String hospital;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RequestStatus status;

  private LocalDate neededBy;

  @Column(nullable = false)
  private int matchedDonors;

  @CreationTimestamp
  @Column(updatable = false)
  private Instant createdAt;
}
