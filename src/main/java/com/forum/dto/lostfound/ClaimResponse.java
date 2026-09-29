package com.forum.dto.lostfound;

import java.time.LocalDateTime;

public record ClaimResponse(Integer id, Integer itemReportId, Integer claimantUserId, String status,
                            boolean confirmedByFinder, boolean confirmedByClaimant, LocalDateTime createdAt,
                            String describedHiddenDetail) {}