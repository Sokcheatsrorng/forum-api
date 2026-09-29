package com.forum.controller;

import com.forum.dto.VoteRequest;
import com.forum.dto.VoteResponse;
import com.forum.security.CustomUserDetails;
import com.forum.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/votes")
@AllArgsConstructor
@Tag(name = "Votes", description = "Vote management endpoints (upvote/downvote)")
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Create vote", description = "Vote on a post (upvote or downvote)")
    @ApiResponse(responseCode = "201", description = "Vote created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<VoteResponse> createVote(
            @Valid @RequestBody VoteRequest voteDTO,
            @AuthenticationPrincipal CustomUserDetails user) {
        Integer userId = user.getId();
        VoteResponse createdVote = voteService.createVote(voteDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVote);
    }

    @PostMapping("/posts/{postId}/upvote")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Upvote a post",
            description = "Upvote a post. If the user already voted, the vote is switched to an upvote.")
    @ApiResponse(responseCode = "201", description = "Upvote recorded successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public ResponseEntity<VoteResponse> upvote(
            @PathVariable Integer postId,
            @AuthenticationPrincipal CustomUserDetails user) {
        Integer voteId = voteService.createUpvote(postId, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(voteService.getVoteById(voteId));
    }

    @PostMapping("/posts/{postId}/downvote")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Downvote a post",
            description = "Downvote a post. If the user already voted, the vote is switched to a downvote.")
    @ApiResponse(responseCode = "201", description = "Downvote recorded successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public ResponseEntity<VoteResponse> downvote(
            @PathVariable Integer postId,
            @AuthenticationPrincipal CustomUserDetails user) {
        Integer voteId = voteService.createDownvote(postId, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(voteService.getVoteById(voteId));
    }

    @GetMapping("/{voteId}")
    @Operation(summary = "Get vote by ID", description = "Retrieve vote information by vote ID")
    @ApiResponse(responseCode = "200", description = "Vote found")
    @ApiResponse(responseCode = "404", description = "Vote not found")
    public ResponseEntity<VoteResponse> getVoteById(@PathVariable Integer voteId) {
        VoteResponse vote = voteService.getVoteById(voteId);
        return ResponseEntity.ok(vote);
    }

    @PutMapping("/{voteId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update vote", description = "Change vote type from upvote to downvote or vice versa")
    @ApiResponse(responseCode = "200", description = "Vote updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Vote not found")
    public ResponseEntity<VoteResponse> updateVote(
            @PathVariable Integer voteId,
            @Valid @RequestBody VoteRequest voteDTO) {
        VoteResponse updatedVote = voteService.updateVote(voteId, voteDTO);
        return ResponseEntity.ok(updatedVote);
    }

    @DeleteMapping("/{voteId}")
    @PreAuthorize("hasRole('USER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Delete vote", description = "Remove a vote from a post")
    @ApiResponse(responseCode = "204", description = "Vote deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Vote not found")
    public ResponseEntity<Void> deleteVote(@PathVariable Integer voteId) {
        voteService.deleteVote(voteId);
        return ResponseEntity.noContent().build();
    }
}