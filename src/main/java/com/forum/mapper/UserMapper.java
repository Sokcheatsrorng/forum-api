package com.forum.mapper;


import com.forum.dto.UserResponse;
import com.forum.dto.users.UserDetailResponse;
import com.forum.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PostMapper.class, CommentMapper.class, BookMarkMapper.class})
public interface UserMapper {

    @Mapping(source = "posts", target = "questions")
    @Mapping(source = "comments", target = "comments")
    @Mapping(source = "bookmark", target = "bookmark")
    UserDetailResponse toUserDetailResponse(User user);
}

//create the thing that you want you create