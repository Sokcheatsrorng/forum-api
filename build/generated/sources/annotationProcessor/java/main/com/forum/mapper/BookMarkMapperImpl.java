package com.forum.mapper;

import com.forum.bookmark.dto.BookMarkResponse;
import com.forum.dto.PostResponse;
import com.forum.dto.UserResponse;
import com.forum.entity.BookMark;
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
public class BookMarkMapperImpl implements BookMarkMapper {

    @Autowired
    private PostMapper postMapper;

    @Override
    public BookMarkResponse toBookMarkResponse(BookMark bookmark) {
        if ( bookmark == null ) {
            return null;
        }

        UserResponse users = null;
        Set<PostResponse> bookMarkList = null;
        Integer id = null;

        users = userToUserResponse( bookmark.getUser() );
        bookMarkList = postSetToPostResponseSet( bookmark.getPosts() );
        id = bookmark.getId();

        BookMarkResponse bookMarkResponse = new BookMarkResponse( id, users, bookMarkList );

        return bookMarkResponse;
    }

    protected UserResponse userToUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setId( user.getId() );
        userResponse.setDisplayName( user.getDisplayName() );
        userResponse.setEmail( user.getEmail() );
        userResponse.setReputation( user.getReputation() );
        userResponse.setViews( user.getViews() );
        userResponse.setUpVotes( user.getUpVotes() );
        userResponse.setDownVotes( user.getDownVotes() );
        userResponse.setCreationDate( user.getCreationDate() );
        userResponse.setLastAccessDate( user.getLastAccessDate() );

        return userResponse;
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
}
