package com.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 300, message = "Title must be between 10 and 300 characters")
    private String title;

    @NotBlank(message = "Body is required")
    @Size(min = 20, message = "Body must have at least 20 characters")
    private String body;

    @NotNull(message = "Post type ID is required")
    private Integer postTypeId;

    private Integer parentId; // For answers

    private Set<Integer> tagIds;
}
