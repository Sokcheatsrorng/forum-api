package com.forum.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Integer id;

    private String title;

    private String body;

    private Integer postTypeId;

    private Integer score;

    private Integer viewCount;

    private Integer ownerId;

    private String ownerDisplayName;

    private Integer parentId;

    private List<TagResponse> tagResponses;

    private List<CommentResponse> comments;

    private LocalDateTime creationDate;

    private LocalDateTime lastActivityDate;

    private LocalDateTime lastEditDate;
}
