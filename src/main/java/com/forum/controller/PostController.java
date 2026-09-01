package com.forum.controller;

import com.forum.dto.PostRequest;
import com.forum.dto.PostResponse;
import com.forum.entity.User;
import com.forum.repository.UserRepository;
import com.forum.security.CustomUserDetails;
import com.forum.media.MediaService;
import com.forum.media.dto.MediaResponse;
import com.forum.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.List;

@RestController
@RequestMapping("/posts")
@AllArgsConstructor
@Tag(name = "Posts", description = "Post management endpoints")
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;
    private final MediaService mediaService;

    private Integer getCurrentUserId(Principal principal) {
        if (principal == null) {
            throw new UsernameNotFoundException("No authenticated user found");
        }

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        User currentUser = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userDetails.getEmail()));

        return currentUser.getId();
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create post", description = "Create a new post (authenticated users only)")
    @ApiResponse(responseCode = "201", description = "Post created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody PostRequest postDTO,
            Principal principal) {

        Integer userId = getCurrentUserId(principal);

        PostResponse createdPost = postService.createPost(postDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PostMapping(value = "/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create post with image files", description = "Submit the post as a JSON request part and optional image files")
    public ResponseEntity<PostResponse> createPostWithImages(
            @Valid @RequestPart("post") PostRequest postDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            Principal principal) {
        if (images != null) {
            if (images.size() > 10) {
                throw new IllegalArgumentException("A post can include at most 10 images");
            }
            if (images.stream().anyMatch(file -> file.isEmpty() || file.getContentType() == null || !file.getContentType().startsWith("image/"))) {
                throw new IllegalArgumentException("Only non-empty image files are allowed");
            }
            LinkedHashSet<String> imageUrls = postDTO.getImageUrls() == null
                    ? new LinkedHashSet<>() : new LinkedHashSet<>(postDTO.getImageUrls());
            mediaService.uploadMultiple(images, "post-images").stream()
                    .map(MediaResponse::uri)
                    .forEach(imageUrls::add);
            postDTO.setImageUrls(imageUrls);
        }
        Integer userId = getCurrentUserId(principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(postDTO, userId));
    }


    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update post", description = "Update post content (post owner only)")
    @ApiResponse(responseCode = "200", description = "Post updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Integer postId,
            @Valid @RequestBody PostRequest postDTO,
            Principal principal) {

        Integer userId = getCurrentUserId(principal);

        PostResponse updatedPost = postService.updatePost(postId, postDTO, userId);
        return ResponseEntity.ok(updatedPost);
    }


    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete post", description = "Delete a post (post owner only)")
    @ApiResponse(responseCode = "204", description = "Post deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public ResponseEntity<Void> deletePost(
            @PathVariable Integer postId,
            Principal principal) {

        Integer userId = getCurrentUserId(principal);

        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Integer postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/sort/score")
    public ResponseEntity<List<PostResponse>> getPostsByScore() {
        return ResponseEntity.ok(postService.getPostsByOrderByScore());
    }

    @GetMapping("/sort/views")
    public ResponseEntity<List<PostResponse>> getPostsByViews() {
        return ResponseEntity.ok(postService.getPostsByOrderByViews());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getPostsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(postService.getPostsByUserId(userId));
    }

    @GetMapping("/type/{postTypeId}")
    public ResponseEntity<List<PostResponse>> getPostsByType(@PathVariable Integer postTypeId) {
        return ResponseEntity.ok(postService.getPostsByPostTypeId(postTypeId));
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<PostResponse>> getPostsByTag(@PathVariable Integer tagId) {
        return ResponseEntity.ok(postService.getPostsByTag(tagId));
    }

    @GetMapping("/answers/{parentId}")
    public ResponseEntity<List<PostResponse>> getAnswers(@PathVariable Integer parentId) {
        return ResponseEntity.ok(postService.getAnswers(parentId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostResponse>> searchPosts(@RequestParam String query) {
        return ResponseEntity.ok(postService.searchPosts(query));
    }

    @GetMapping("/search/relevance")
    public ResponseEntity<List<PostResponse>> searchPostsByRelevance(@RequestParam String query) {
        return ResponseEntity.ok(postService.searchPostsByRelevance(query));
    }
}
