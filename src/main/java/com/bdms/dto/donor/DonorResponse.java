package com.bdms.dto.donor;

import com.bdms.entity.Role;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonorResponse {
    private Long id;
    private String name;
    private String email;
    private String bloodGroup;
    private String location;
    private String phoneNumber;
    private Boolean availabilityStatus;
    private Long hospitalId;
    private String hospitalName;
    private LocalDate lastDonationDate;
    private Integer totalDonations;
    private Role role;
}
