package com.bdms.dto.admin;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsResponse {

    private long totalDonors;
    private long totalRequests;
    private long totalCompletedDonations;
    private long activeDonorsCount;
    private Map<String, Long> monthlyDonationStats;
}
