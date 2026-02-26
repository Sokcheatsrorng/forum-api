package com.forum.mapper;


import com.forum.bookmark.dto.BookMarkRequest;
import com.forum.bookmark.dto.BookMarkResponse;
import com.forum.entity.BookMark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {PostMapper.class})
public interface BookMarkMapper {

   @Mapping(source = "user", target = "users")
   @Mapping(source = "posts", target = "bookMarkList")
   BookMarkResponse toBookMarkResponse(BookMark bookmark);
}