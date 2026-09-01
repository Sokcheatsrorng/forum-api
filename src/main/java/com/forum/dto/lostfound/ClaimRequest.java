package com.forum.dto.lostfound;
import jakarta.validation.constraints.NotBlank;
public record ClaimRequest(@NotBlank String describedHiddenDetail) {}
