package com.forum.mapper;

import com.forum.dto.CommentResponse;
import com.forum.entity.Comment;
import com.forum.entity.Post;
import com.forum.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-25T22:30:08+0700",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.2.jar, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public CommentResponse toResponse(Comment comment) {
        if ( comment == null ) {
            return null;
        }

        CommentResponse commentResponse = new CommentResponse();

        commentResponse.setPostId( commentPostId( comment ) );
        commentResponse.setUserId( commentUserId( comment ) );
        commentResponse.setUserDisplayName( commentUserDisplayName( comment ) );
        commentResponse.setId( comment.getId() );
        commentResponse.setText( comment.getText() );
        commentResponse.setScore( comment.getScore() );
        commentResponse.setCreationDate( comment.getCreationDate() );
        commentResponse.setLastEditDate( comment.getLastEditDate() );

        return commentResponse;
    }

    private Integer commentPostId(Comment comment) {
        if ( comment == null ) {
            return null;
        }
        Post post = comment.getPost();
        if ( post == null ) {
            return null;
        }
        Integer id = post.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Integer commentUserId(Comment comment) {
        if ( comment == null ) {
            return null;
        }
        User user = comment.getUser();
        if ( user == null ) {
            return null;
        }
        Integer id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String commentUserDisplayName(Comment comment) {
        if ( comment == null ) {
            return null;
        }
        User user = comment.getUser();
        if ( user == null ) {
            return null;
        }
        String displayName = user.getDisplayName();
        if ( displayName == null ) {
            return null;
        }
        return displayName;
    }
}
