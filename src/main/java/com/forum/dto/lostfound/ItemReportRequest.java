package com.forum.dto.lostfound;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public record ItemReportRequest(
        @NotBlank String itemType, @NotBlank String title, Integer categoryId, String description,
        @NotNull LocalDate itemDate, @NotBlank String scope, Integer locationId,
        BigDecimal mapLat, BigDecimal mapLng, String freeTextLocation, String photoUrl, String hiddenDetail) {}
