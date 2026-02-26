package com.forum.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponse {

    private Integer id;

    private Integer postId;

//    private Integer totalDownVotes;
//
//    private Integer totalUpVotes;

    private Integer upVoteId;

    private Integer voteTypeId;

    private Integer userId;

    private LocalDateTime creationDate;
}
