package com.forum.mapper;

import com.forum.dto.CommentResponse;
import com.forum.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "postId", source = "post.id")
//    @Mapping(target = "posts", ignore = true)
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.displayName", target = "userDisplayName")
    CommentResponse toResponse(Comment comment);
}