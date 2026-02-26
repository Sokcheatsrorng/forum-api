package com.forum.controller;

import com.forum.dto.VoteRequest;
import com.forum.dto.VoteResponse;
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
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

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
            Principal principal) {
        Integer userId = 1; // Replace with actual user extraction logic
        VoteResponse createdVote = voteService.createVote(voteDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVote);
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

//    @GetMapping("/post/{postId}")
//    @Operation(summary = "Get post votes", description = "Retrieve all votes for a specific post")
//    @ApiResponse(responseCode = "200", description = "Votes retrieved successfully")
//    public ResponseEntity<List<VoteResponse>> getVotesByPostId(@PathVariable Integer postId) {
//        List<VoteResponse> votes = voteService.getVotesByPostId(postId);
//        return ResponseEntity.ok(votes);
//    }
//
//    @GetMapping("/user/{userId}")
//    @Operation(summary = "Get user's votes", description = "Retrieve all votes created by a specific user")
//    @ApiResponse(responseCode = "200", description = "Votes retrieved successfully")
//    public ResponseEntity<List<VoteResponse>> getVotesByUserId(@PathVariable Integer userId) {
//        List<VoteResponse> votes = voteService.getVotesByPostId(userId);
//        return ResponseEntity.ok(votes);
//    }
}
