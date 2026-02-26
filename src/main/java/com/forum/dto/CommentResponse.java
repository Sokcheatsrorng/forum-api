package com.forum.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Integer id;
    private Integer postId;
    private String text;
    private Integer score;
    private Integer userId;
    private String userDisplayName;
    private LocalDateTime creationDate;
    private LocalDateTime lastEditDate;
}
