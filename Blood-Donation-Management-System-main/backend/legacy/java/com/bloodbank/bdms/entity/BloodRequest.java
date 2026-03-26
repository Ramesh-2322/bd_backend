package com.bloodbank.bdms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "blood_request")
@Getter
@Setter
@NoArgsConstructor
public class BloodRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    private String state;
    private String city;
    private String address;

    @ManyToOne(optional = false)
    @JoinColumn(name = "blood_group_id")
    private BloodGroup bloodGroup;

    private String requestDate;
}
