package com.forum.mapper;

import com.forum.dto.CommentResponse;
import com.forum.entity.Comment;
import com.forum.entity.Post;
import com.forum.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-01T12:01:14+0700",
    comments = "version: 1.6.2, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.10.2.jar, environment: Java 23.0.1 (Oracle Corporation)"
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
        Post post = comment.getPost();
        if ( post == null ) {
            return null;
        }
        return post.getId();
    }

    private Integer commentUserId(Comment comment) {
        User user = comment.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    private String commentUserDisplayName(Comment comment) {
        User user = comment.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getDisplayName();
    }
}
