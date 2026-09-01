package com.forum.mapper;

import com.forum.dto.CommentResponse;
import com.forum.dto.PostResponse;
import com.forum.dto.TagResponse;
import com.forum.entity.Comment;
import com.forum.entity.Post;
import com.forum.entity.PostType;
import com.forum.entity.Tag;
import com.forum.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-01T12:01:14+0700",
    comments = "version: 1.6.2, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.2.jar, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class PostMapperImpl implements PostMapper {

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public PostResponse toResponse(Post post) {
        if ( post == null ) {
            return null;
        }

        PostResponse postResponse = new PostResponse();

        postResponse.setOwnerId( postOwnerId( post ) );
        postResponse.setOwnerDisplayName( postOwnerDisplayName( post ) );
        postResponse.setPostTypeId( postPostTypeId( post ) );
        postResponse.setTagResponses( tagSetToTagResponseList( post.getTags() ) );
        postResponse.setComments( commentSetToCommentResponseList( post.getComments() ) );
        postResponse.setId( post.getId() );
        postResponse.setTitle( post.getTitle() );
        postResponse.setBody( post.getBody() );
        postResponse.setCodeSnippet( post.getCodeSnippet() );
        postResponse.setCodeLanguage( post.getCodeLanguage() );
        Set<String> set = post.getImageUrls();
        if ( set != null ) {
            postResponse.setImageUrls( new ArrayList<String>( set ) );
        }
        postResponse.setScore( post.getScore() );
        postResponse.setViewCount( post.getViewCount() );
        postResponse.setParentId( post.getParentId() );
        postResponse.setCreationDate( post.getCreationDate() );
        postResponse.setLastActivityDate( post.getLastActivityDate() );
        postResponse.setLastEditDate( post.getLastEditDate() );

        return postResponse;
    }

    private Integer postOwnerId(Post post) {
        User owner = post.getOwner();
        if ( owner == null ) {
            return null;
        }
        return owner.getId();
    }

    private String postOwnerDisplayName(Post post) {
        User owner = post.getOwner();
        if ( owner == null ) {
            return null;
        }
        return owner.getDisplayName();
    }

    private Integer postPostTypeId(Post post) {
        PostType postType = post.getPostType();
        if ( postType == null ) {
            return null;
        }
        return postType.getId();
    }

    protected List<TagResponse> tagSetToTagResponseList(Set<Tag> set) {
        if ( set == null ) {
            return null;
        }

        List<TagResponse> list = new ArrayList<TagResponse>( set.size() );
        for ( Tag tag : set ) {
            list.add( tagMapper.toResponse( tag ) );
        }

        return list;
    }

    protected List<CommentResponse> commentSetToCommentResponseList(Set<Comment> set) {
        if ( set == null ) {
            return null;
        }

        List<CommentResponse> list = new ArrayList<CommentResponse>( set.size() );
        for ( Comment comment : set ) {
            list.add( commentMapper.toResponse( comment ) );
        }

        return list;
    }
}
