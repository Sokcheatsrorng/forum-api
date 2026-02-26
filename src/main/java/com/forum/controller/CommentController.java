package com.forum.controller;

import com.forum.dto.CommentRequest;
import com.forum.dto.CommentResponse;
import com.forum.entity.Comment;
import com.forum.service.CommentService;
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
@RequestMapping("/comments")
@AllArgsConstructor
@Tag(name = "Comments", description = "Comment management endpoints")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create comment", description = "Add a new comment to a post (authenticated users only)")
    @ApiResponse(responseCode = "201", description = "Comment created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<CommentResponse> createComment(
            @Valid @RequestBody CommentRequest commentDTO,
            Principal principal) {
        Integer userId = 1; // Replace with actual user extraction logic
        CommentResponse createdComment = commentService.createComment(commentDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "Get comment by ID", description = "Retrieve comment information by comment ID")
    @ApiResponse(responseCode = "200", description = "Comment found")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable Integer commentId) {
        CommentResponse comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update comment", description = "Update comment content (comment owner only)")
    @ApiResponse(responseCode = "200", description = "Comment updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Integer commentId,
            @Valid @RequestBody CommentRequest commentDTO,
            Principal principal) {
        Integer userId = 1; // Replace with actual user extraction logic
        CommentResponse updatedComment = commentService.updateComment(commentId, commentDTO, userId);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete comment", description = "Delete a comment (comment owner only)")
    @ApiResponse(responseCode = "204", description = "Comment deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer commentId,
            Principal principal) {
        Integer userId = 1; // Replace with actual user extraction logic
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/post/{postId}")
    @Operation(summary = "Get post comments", description = "Retrieve all comments for a specific post")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(@PathVariable Integer postId) {
        List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's comments", description = "Retrieve all comments created by a specific user")
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    public ResponseEntity<List<CommentResponse>> getCommentsByUserId(@PathVariable Integer userId) {
        List<CommentResponse> comments = commentService.getCommentsByUserId(userId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/search")
    @Operation(summary = "Search comments", description = "Search comments by text content")
    @ApiResponse(responseCode = "200", description = "Search results returned")
    public ResponseEntity<List<CommentResponse>> searchComments(@RequestParam String query) {
        List<CommentResponse> comments = commentService.searchComments(query);
        return ResponseEntity.ok(comments);
    }
}
