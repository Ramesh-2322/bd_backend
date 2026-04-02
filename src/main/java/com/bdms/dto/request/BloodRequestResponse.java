package com.bdms.dto.request;

import com.bdms.entity.RequestStatus;
import com.bdms.entity.UrgencyLevel;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BloodRequestResponse {

    private Long id;
    private String patientName;
    private String bloodGroup;
    private Integer unitsRequired;
    private String hospitalName;
    private String location;
    private UrgencyLevel urgencyLevel;
    private RequestStatus status;
    private Long requestedById;
    private String requestedByName;
    private LocalDateTime createdAt;
}
