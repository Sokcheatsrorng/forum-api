package com.forum.bookmark.dto;

import com.forum.dto.PostResponse;
import com.forum.dto.UserResponse;

import java.util.List;
import java.util.Set;

public record BookMarkResponse(

        Integer id,

        UserResponse users,

        Set<PostResponse> bookMarkList
) {
}
