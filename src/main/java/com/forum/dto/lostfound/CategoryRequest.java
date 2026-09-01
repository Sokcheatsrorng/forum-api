package com.forum.dto.lostfound;
import jakarta.validation.constraints.NotBlank;
public record CategoryRequest(@NotBlank String name) {}
