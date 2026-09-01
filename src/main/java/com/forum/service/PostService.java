package com.forum.service;

import com.forum.dto.PostRequest;
import com.forum.dto.PostResponse;
import com.forum.dto.TagResponse;
import com.forum.dto.CommentResponse;
import com.forum.entity.Post;
import com.forum.entity.PostType;
import com.forum.entity.Tag;
import com.forum.entity.Comment;
import com.forum.exception.ResourceNotFoundException;
import com.forum.repository.PostRepository;
import com.forum.repository.PostTypeRepository;
import com.forum.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final PostTypeRepository postTypeRepository;
    private final TagRepository tagRepository;
    private final UserService userService;

    // ─────────────────────────────────────────────
    //               CREATE
    // ─────────────────────────────────────────────
    public PostResponse createPost(PostRequest postDTO, Integer userId) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setBody(postDTO.getBody());
        post.setCodeSnippet(postDTO.getCodeSnippet());
        post.setCodeLanguage(postDTO.getCodeLanguage());
        if (postDTO.getImageUrls() != null) {
            post.setImageUrls(new LinkedHashSet<>(postDTO.getImageUrls()));
        }
        post.setOwner(userService.getUserEntityById(userId));
        post.setCreationDate(LocalDateTime.now());
        post.setLastActivityDate(LocalDateTime.now());
        post.setLastEditDate(LocalDateTime.now());

        PostType postType = postTypeRepository.findById(postDTO.getPostTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("PostType not found with id: " + postDTO.getPostTypeId()));
        post.setPostType(postType);

        if (postDTO.getParentId() != null) {
            post.setParentId(postDTO.getParentId());
        }

        if (postDTO.getTagIds() != null && !postDTO.getTagIds().isEmpty()) {
            Set<Tag> tags = postDTO.getTagIds().stream()
                    .map(id -> tagRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id)))
                    .collect(Collectors.toSet());
            post.setTags(tags);
        }

        Post savedPost = postRepository.save(post);
        return mapToDTO(savedPost);
    }

    // ─────────────────────────────────────────────
    //               READ
    // ─────────────────────────────────────────────
    public PostResponse getPostById(Integer postId) {
        Post post = postRepository.findFullById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        return mapToDTO(post);
    }

    public Post getPostEntityById(Integer postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }

    // ─────────────────────────────────────────────
    //               UPDATE / DELETE
    // ─────────────────────────────────────────────
    public PostResponse updatePost(Integer postId, PostRequest postDTO, Integer userId) {
        Post post = getPostEntityById(postId);

        if (!post.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Only the post owner can update the post");
        }

        post.setTitle(postDTO.getTitle());
        post.setBody(postDTO.getBody());
        post.setCodeSnippet(postDTO.getCodeSnippet());
        post.setCodeLanguage(postDTO.getCodeLanguage());
        post.setLastEditDate(LocalDateTime.now());
        post.setLastActivityDate(LocalDateTime.now());

        if (postDTO.getTagIds() != null) {
            Set<Tag> tags = postDTO.getTagIds().isEmpty()
                    ? Set.of()
                    : postDTO.getTagIds().stream()
                    .map(id -> tagRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id)))
                    .collect(Collectors.toSet());
            post.setTags(tags);
        }

        if (postDTO.getImageUrls() != null) {
            post.setImageUrls(new LinkedHashSet<>(postDTO.getImageUrls()));
        }

        return mapToDTO(postRepository.save(post));
    }

    public void deletePost(Integer postId, Integer userId) {
        Post post = getPostEntityById(postId);

        if (!post.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Only the post owner can delete the post");
        }

        postRepository.deleteById(postId);
    }

    // ─────────────────────────────────────────────
    //               LIST / SEARCH METHODS
    // ─────────────────────────────────────────────
    public List<PostResponse> getAllPosts() {
        return postRepository.findAllOrderByNewest().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getPostsByOrderByScore() {
        return postRepository.findAllOrderByScore().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getPostsByOrderByViews() {
        return postRepository.findAllOrderByViews().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getPostsByUserId(Integer userId) {
        return postRepository.findByOwnerId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getPostsByPostTypeId(Integer postTypeId) {
        return postRepository.findByPostTypeId(postTypeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PostResponse> searchPosts(String searchTerm) {
        return postRepository.searchPosts(searchTerm).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PostResponse> searchPostsByRelevance(String searchTerm) {
        return postRepository.searchPostsByRelevance(searchTerm).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getPostsByTag(Integer tagId) {
        return postRepository.findByTagId(tagId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getAnswers(Integer parentId) {
        return postRepository.findByParentId(parentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────
    //               SCORE / VOTES
    // ─────────────────────────────────────────────
    public void incrementScore(Integer postId, Integer points) {
        Post post = getPostEntityById(postId);
        post.setScore(post.getScore() + points);
        post.setLastActivityDate(LocalDateTime.now());
        postRepository.save(post);
    }

    // ─────────────────────────────────────────────
    //               MAPPING
    // ─────────────────────────────────────────────
    private PostResponse mapToDTO(Post post) {
        PostResponse dto = new PostResponse();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setBody(post.getBody());
        dto.setCodeSnippet(post.getCodeSnippet());
        dto.setCodeLanguage(post.getCodeLanguage());
        dto.setImageUrls(post.getImageUrls().stream().toList());
        dto.setPostTypeId(post.getPostType().getId());
        dto.setScore(post.getScore() != null ? post.getScore() : 0);
        dto.setViewCount(post.getViewCount() != null ? post.getViewCount() : 0);
        dto.setOwnerId(post.getOwner().getId());
        dto.setOwnerDisplayName(post.getOwner().getDisplayName());
        dto.setParentId(post.getParentId());
        dto.setCreationDate(post.getCreationDate());
        dto.setLastActivityDate(post.getLastActivityDate());
        dto.setLastEditDate(post.getLastEditDate());

        // Tags
        List<TagResponse> tagDtos = post.getTags().stream()
                .map(tag -> new TagResponse(
                        tag.getId(),
                        tag.getTagName(),
                        tag.getCount(),
                        tag.getExcerptPostId(),
                        tag.getWikiPostId()
                ))
                .collect(Collectors.toList());
        dto.setTagResponses(tagDtos);

        // Comments
        List<CommentResponse> commentDtos = post.getComments().stream()
                .map(this::mapCommentToResponse)
                .collect(Collectors.toList());
        dto.setComments(commentDtos);

        return dto;
    }

    private CommentResponse mapCommentToResponse(Comment comment) {
        CommentResponse dto = new CommentResponse();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPost().getId());
        dto.setText(comment.getText());
        dto.setScore(comment.getScore() != null ? comment.getScore() : 0);
        dto.setUserId(comment.getUser().getId());
        dto.setUserDisplayName(comment.getUser().getDisplayName());
        dto.setCreationDate(comment.getCreationDate());
        dto.setLastEditDate(comment.getLastEditDate());
        return dto;
    }
}
