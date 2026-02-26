package com.forum.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequest {

    @NotNull(message = "Post ID is required")
    private Integer postId;

    @NotNull(message = "Vote type ID is required")
    private Integer voteTypeId;
}
