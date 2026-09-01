package com.forum.dto.lostfound;
import java.math.BigDecimal;
public record MatchResponse(Integer id, Integer lostItemId, Integer foundItemId, BigDecimal totalScore, String status) {}
