package com.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
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

    @Size(max = 20000, message = "Code snippet must not exceed 20,000 characters")
    private String codeSnippet;

    @Size(max = 100, message = "Code language must not exceed 100 characters")
    private String codeLanguage;

    @NotNull(message = "Post type ID is required")
    private Integer postTypeId;

    private Integer parentId; // For answers

    private Set<Integer> tagIds;

    @Size(max = 10, message = "A post can include at most 10 images")
    private Set<@Pattern(regexp = "https?://.+", message = "Image URL must be an absolute HTTP(S) URL") String> imageUrls;
}
