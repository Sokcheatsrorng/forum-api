package com.forum.controller;

import com.forum.dto.CommentRequest;
import com.forum.dto.CommentResponse;
import com.forum.entity.User;
import com.forum.repository.UserRepository;
import com.forum.security.CustomUserDetails;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/comments")
@AllArgsConstructor
@Tag(name = "Comments", description = "Comment management endpoints")
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;

    // ==================== HELPER METHOD (same as PostController) ====================
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

    // =========================== CREATE COMMENT ===========================
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

        Integer userId = getCurrentUserId(principal);

        CommentResponse createdComment = commentService.createComment(commentDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    // =========================== UPDATE COMMENT ===========================
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

        Integer userId = getCurrentUserId(principal);

        CommentResponse updatedComment = commentService.updateComment(commentId, commentDTO, userId);
        return ResponseEntity.ok(updatedComment);
    }

    // =========================== DELETE COMMENT ===========================
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

        Integer userId = getCurrentUserId(principal);

        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    // ======================== PUBLIC ENDPOINTS (clean & consistent) ========================
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable Integer commentId) {
        return ResponseEntity.ok(commentService.getCommentById(commentId));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(@PathVariable Integer postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(commentService.getCommentsByUserId(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CommentResponse>> searchComments(@RequestParam String query) {
        return ResponseEntity.ok(commentService.searchComments(query));
    }
}