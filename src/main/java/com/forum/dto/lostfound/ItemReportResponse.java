package com.forum.dto.lostfound;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
public record ItemReportResponse(Integer id, Integer userId, String itemType, String title, Integer categoryId,
                                 String categoryName, String description, LocalDate itemDate, String scope,
                                 Integer locationId, String locationLabel, BigDecimal mapLat, BigDecimal mapLng,
                                 String freeTextLocation, String photoUrl, String status, String moderationStatus,
                                 LocalDate expiryDate, LocalDateTime createdAt) {}
