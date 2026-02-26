package com.forum.bookmark;

import com.forum.bookmark.dto.BookMarkRequest;
import com.forum.bookmark.dto.BookMarkResponse;
import com.forum.dto.CommentResponse;
import com.forum.dto.PostResponse;
import com.forum.dto.TagResponse;
import com.forum.entity.BookMark;
import com.forum.entity.Post;
import com.forum.entity.User;
import com.forum.mapper.BookMarkMapper;
import com.forum.repository.PostRepository;
import com.forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookMarkServiceImpl implements BookMarkService {

    private final BookMarkRepository bookMarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final BookMarkMapper bookMarkMapper;

    // helper: get authenticated user
    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not authenticated"));
    }

    // helper: get or create bookmark for user
    private BookMark getOrCreateBookmark(User user) {
        return bookMarkRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    BookMark bookmark = new BookMark();
                    bookmark.setUser(user);
                    return bookMarkRepository.save(bookmark);
                });
    }

//    private BookMarkResponse toResponse(BookMark bookmark) {
//        Set<PostResponse> postResponses = bookmark.getPosts()
//                .stream()
//                .map(post -> PostResponse.builder()
//                        .id(post.getId())
//                        .title(post.getTitle())
//                        .body(post.getBody())
//                        .postTypeId(post.getPostType() != null ? post.getPostType().getId() : null)
//                        .score(post.getScore())
//                        .viewCount(post.getViewCount())
//                        .ownerId(post.getOwner() != null ? post.getOwner().getId() : null)
//                        .ownerDisplayName(post.getOwner() != null ? post.getOwner().getDisplayName() : null)
//                        .parentId(post.getParentId() != null ? post.getParentId() : null)
//                        .tagResponses(post.getTags().stream()
//                                .map(tag -> TagResponse.builder()
//                                        .id(tag.getId())
//                                        .tagName(tag.getTagName())
//                                        .build())
//                                .toList())
//                        .comments(post.getComments().stream()
//                                .map(comment -> CommentResponse.builder()
//                                        .id(comment.getId())
//                                        .body(comment.getText())
//                                        .build())
//                                .toList())
//                        .creationDate(post.getCreationDate())
//                        .lastActivityDate(post.getLastActivityDate())
//                        .lastEditDate(post.getLastEditDate())
//                        .build()
//                )
//                .collect(Collectors.toSet());
//
//        return new BookMarkResponse(
//                bookmark.getId(),
//                bookmark.getUser(),
//                postResponses
//        );
//    }
    @Override
    public BookMarkResponse addBookMark(BookMarkRequest request) {
        User user = getAuthenticatedUser();
        BookMark bookmark = getOrCreateBookmark(user);

        Set<Post> postsToAdd = request.postIds().stream()
                .map(id -> postRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Post not found with id: " + id)))
                .collect(Collectors.toSet());

        bookmark.getPosts().addAll(postsToAdd);
        return bookMarkMapper.toBookMarkResponse(bookMarkRepository.save(bookmark));
    }

    @Override
    public BookMarkResponse removeBookMark(BookMarkRequest request) {
        User user = getAuthenticatedUser();
        BookMark bookmark = bookMarkRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Bookmark not found"));

        request.postIds().forEach(id -> bookmark.getPosts()
                .removeIf(post -> post.getId().equals(id)));

        return bookMarkMapper.toBookMarkResponse(bookMarkRepository.save(bookmark));
    }

    @Override
    public BookMarkResponse getMyBookmarks() {
        User user = getAuthenticatedUser();
        BookMark bookmark = getOrCreateBookmark(user);
        return bookMarkMapper.toBookMarkResponse(bookmark);
    }

}