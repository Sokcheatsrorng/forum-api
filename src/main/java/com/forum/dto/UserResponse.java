package com.forum.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Integer id;

    private String displayName;

    private String email;

    private Integer reputation;

    private Integer views;

    private Integer upVotes;

    private Integer downVotes;

    private LocalDateTime creationDate;

    private LocalDateTime lastAccessDate;
}
