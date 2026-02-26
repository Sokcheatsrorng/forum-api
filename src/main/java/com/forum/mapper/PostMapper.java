package com.forum.mapper;

import com.forum.dto.PostResponse;
import com.forum.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TagMapper.class, CommentMapper.class})
public interface PostMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "owner.displayName", target = "ownerDisplayName")
    @Mapping(source = "postType.id", target = "postTypeId")
    @Mapping(source = "tags", target = "tagResponses")
    @Mapping(source = "comments", target = "comments")
//    @Mapping(target = "acceptedAnswer", ignore = true)  // Prevent self-recursion if acceptedAnswer is mapped
    PostResponse toResponse(Post post);
}