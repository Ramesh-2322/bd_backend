package com.bloodbank.bdms.entity;

import com.bloodbank.bdms.entity.enums.BloodGroup;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "donor_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonorProfile {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BloodGroup bloodGroup;

  @Column(nullable = false)
  private String gender;

  private LocalDate dateOfBirth;

  private String address;

  private String city;

  private LocalDate lastDonationDate;

  private LocalDate nextEligibleDate;

  @Column(nullable = false)
  private boolean available;

  @Column(nullable = false)
  private int totalDonations;

  @CreationTimestamp
  @Column(updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  private Instant updatedAt;
}
