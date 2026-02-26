package com.forum.controller;

import com.forum.dto.PostRequest;
import com.forum.dto.PostResponse;
import com.forum.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/posts")
@AllArgsConstructor
@Tag(name = "Posts", description = "Post management endpoints")
public class PostController {

    private final PostService postService;

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
        // Extract userId from principal (implementation depends on your security setup)
        Integer userId = 1; // Replace with actual user extraction logic
        PostResponse createdPost = postService.createPost(postDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Get post by ID", description = "Retrieve post information by post ID")
    @ApiResponse(responseCode = "200", description = "Post found")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Integer postId) {
        PostResponse post = postService.getPostById(postId);
        return ResponseEntity.ok(post);
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
        Integer userId = 1; // Replace with actual user extraction logic
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
        Integer userId = 1; // Replace with actual user extraction logic
        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get all posts", description = "Retrieve all posts ordered by creation date (newest first)")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/sort/score")
    @Operation(summary = "Get posts by score", description = "Retrieve posts ordered by score (highest first)")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public ResponseEntity<List<PostResponse>> getPostsByScore() {
        List<PostResponse> posts = postService.getPostsByOrderByScore();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/sort/views")
    @Operation(summary = "Get posts by views", description = "Retrieve posts ordered by view count (highest first)")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public ResponseEntity<List<PostResponse>> getPostsByViews() {
        List<PostResponse> posts = postService.getPostsByOrderByViews();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's posts", description = "Retrieve all posts created by a specific user")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public ResponseEntity<List<PostResponse>> getPostsByUserId(@PathVariable Integer userId) {
        List<PostResponse> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/type/{postTypeId}")
    @Operation(summary = "Get posts by type", description = "Retrieve posts by post type (Question, Answer)")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public ResponseEntity<List<PostResponse>> getPostsByType(@PathVariable Integer postTypeId) {
        List<PostResponse> posts = postService.getPostsByPostTypeId(postTypeId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/tag/{tagId}")
    @Operation(summary = "Get posts by tag", description = "Retrieve posts with a specific tag")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public ResponseEntity<List<PostResponse>> getPostsByTag(@PathVariable Integer tagId) {
        List<PostResponse> posts = postService.getPostsByTag(tagId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/answers/{parentId}")
    @Operation(summary = "Get post answers", description = "Retrieve all answers to a specific question")
    @ApiResponse(responseCode = "200", description = "Answers retrieved successfully")
    public ResponseEntity<List<PostResponse>> getAnswers(@PathVariable Integer parentId) {
        List<PostResponse> answers = postService.getAnswers(parentId);
        return ResponseEntity.ok(answers);
    }

    @GetMapping("/search")
    @Operation(summary = "Search posts", description = "Search posts by title or body content")
    @ApiResponse(responseCode = "200", description = "Search results returned")
    public ResponseEntity<List<PostResponse>> searchPosts(@RequestParam String query) {
        List<PostResponse> posts = postService.searchPosts(query);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search/relevance")
    @Operation(summary = "Search posts by relevance", description = "Search posts ordered by relevance/score")
    @ApiResponse(responseCode = "200", description = "Search results returned")
    public ResponseEntity<List<PostResponse>> searchPostsByRelevance(@RequestParam String query) {
        List<PostResponse> posts = postService.searchPostsByRelevance(query);
        return ResponseEntity.ok(posts);
    }
}
