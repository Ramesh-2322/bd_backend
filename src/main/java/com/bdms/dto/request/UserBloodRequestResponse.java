package com.bdms.dto.request;

import com.bdms.entity.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBloodRequestResponse {

    private Long id;
    private Long userId;
    private String bloodGroup;
    private Integer units;
    private RequestStatus status;
    private String location;
    private LocalDateTime createdAt;
}
