package com.forum.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
public class TagResponse {

    private Integer id;
    private String tagName;
    private Integer count;
    private String excerptPostId;
    private Integer wikiPostId;

}
