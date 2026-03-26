package com.bloodbank.bdms.entity;

import com.bloodbank.bdms.entity.enums.DonationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "donor_id", nullable = false)
  private DonorProfile donor;

  @Column(nullable = false)
  private LocalDate donationDate;

  @Column(nullable = false)
  private int quantityMl;

  private String center;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DonationStatus status;

  private String notes;

  @CreationTimestamp
  @Column(updatable = false)
  private Instant createdAt;
}
