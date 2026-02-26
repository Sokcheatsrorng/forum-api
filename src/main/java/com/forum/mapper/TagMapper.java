package com.forum.mapper;

import com.forum.dto.TagResponse;
import com.forum.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {

    @Mapping(source = "tagName", target = "tagName")        // exact match
    @Mapping(source = "count", target = "count")
    @Mapping(source = "excerptPostId", target = "excerptPostId")
    @Mapping(source = "wikiPostId", target = "wikiPostId")
    TagResponse toResponse(Tag tag);
}