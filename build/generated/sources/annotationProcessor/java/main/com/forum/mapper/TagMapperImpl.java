package com.forum.mapper;

import com.forum.dto.TagResponse;
import com.forum.entity.Tag;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-25T22:30:08+0700",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.2.jar, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class TagMapperImpl implements TagMapper {

    @Override
    public TagResponse toResponse(Tag tag) {
        if ( tag == null ) {
            return null;
        }

        String tagName = null;
        Integer count = null;
        String excerptPostId = null;
        Integer wikiPostId = null;
        Integer id = null;

        tagName = tag.getTagName();
        count = tag.getCount();
        excerptPostId = tag.getExcerptPostId();
        wikiPostId = tag.getWikiPostId();
        id = tag.getId();

        TagResponse tagResponse = new TagResponse( id, tagName, count, excerptPostId, wikiPostId );

        return tagResponse;
    }
}
