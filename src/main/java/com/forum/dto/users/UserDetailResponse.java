package com.forum.dto.users;

import com.forum.bookmark.dto.BookMarkResponse;
import com.forum.dto.CommentResponse;
import com.forum.dto.PostResponse;
import lombok.Builder;

import java.util.List;
import java.util.Set;


@Builder
public record UserDetailResponse(
        Integer id,

        String email,

        String displayName,

        String profileImage,

        String bio,

        Set<PostResponse> questions,
        // add this
        Set<CommentResponse> comments,
        // add this
        BookMarkResponse bookmark          // add this
) {
}
