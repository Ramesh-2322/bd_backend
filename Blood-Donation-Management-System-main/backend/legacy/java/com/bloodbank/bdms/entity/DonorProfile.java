package com.bloodbank.bdms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "donor_profile")
@Getter
@Setter
@NoArgsConstructor
public class DonorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(nullable = false)
    private String dateOfBirth;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false, length = 500)
    private String address;

    @ManyToOne(optional = false)
    @JoinColumn(name = "blood_group_id")
    private BloodGroup bloodGroup;

    @Column(nullable = false, length = 10)
    private String gender;

    private String imagePath;

    private boolean readyToDonate = true;
}
