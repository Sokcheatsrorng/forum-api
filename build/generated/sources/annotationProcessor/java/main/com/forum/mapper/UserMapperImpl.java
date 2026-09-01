package com.forum.mapper;

import com.forum.dto.CommentResponse;
import com.forum.dto.PostResponse;
import com.forum.dto.users.UserDetailResponse;
import com.forum.entity.Comment;
import com.forum.entity.Post;
import com.forum.entity.User;
import java.util.LinkedHashSet;
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
public class UserMapperImpl implements UserMapper {

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private BookMarkMapper bookMarkMapper;

    @Override
    public UserDetailResponse toUserDetailResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserDetailResponse.UserDetailResponseBuilder userDetailResponse = UserDetailResponse.builder();

        userDetailResponse.questions( postSetToPostResponseSet( user.getPosts() ) );
        userDetailResponse.comments( commentSetToCommentResponseSet( user.getComments() ) );
        userDetailResponse.bookmark( bookMarkMapper.toBookMarkResponse( user.getBookmark() ) );
        userDetailResponse.id( user.getId() );
        userDetailResponse.email( user.getEmail() );
        userDetailResponse.displayName( user.getDisplayName() );
        userDetailResponse.profileImage( user.getProfileImage() );
        userDetailResponse.bio( user.getBio() );

        return userDetailResponse.build();
    }

    protected Set<PostResponse> postSetToPostResponseSet(Set<Post> set) {
        if ( set == null ) {
            return null;
        }

        Set<PostResponse> set1 = new LinkedHashSet<PostResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Post post : set ) {
            set1.add( postMapper.toResponse( post ) );
        }

        return set1;
    }

    protected Set<CommentResponse> commentSetToCommentResponseSet(Set<Comment> set) {
        if ( set == null ) {
            return null;
        }

        Set<CommentResponse> set1 = new LinkedHashSet<CommentResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Comment comment : set ) {
            set1.add( commentMapper.toResponse( comment ) );
        }

        return set1;
    }
}
