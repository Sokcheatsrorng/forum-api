package com.forum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotNull(message = "Post ID is required")
    private Integer postId;

    @NotBlank(message = "Comment text is required")
    @Size(min = 5, max = 500, message = "Comment must be between 5 and 500 characters")
    private String text;
}
