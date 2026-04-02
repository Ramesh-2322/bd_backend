package com.bdms.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchedDonorResponse {

    private Long id;
    private String name;
    private String email;
    private String bloodGroup;
    private String location;
    private String phoneNumber;
    private Boolean availabilityStatus;
}
