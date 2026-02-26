package com.forum.bookmark.dto;

import com.forum.entity.Post;

import java.util.List;

public record BookMarkRequest(

        List<Integer> postIds
) {
}
