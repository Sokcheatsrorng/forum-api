package com.forum.service;

import com.forum.dto.VoteRequest;
import com.forum.dto.VoteResponse;
import com.forum.entity.Vote;
import com.forum.entity.VoteType;
import com.forum.entity.NotificationType;
import com.forum.exception.ResourceNotFoundException;
import com.forum.repository.VoteRepository;
import com.forum.repository.VoteTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class VoteService {

    private static final String UP = "UpMod";
    private static final String DOWN = "DownMod";

    private final VoteRepository voteRepository;
    private final VoteTypeRepository voteTypeRepository;
    private final PostService postService;
    private final UserService userService;
    private final NotificationService notificationService;

    public VoteResponse createVote(VoteRequest voteDTO, Integer userId) {
        Optional<Vote> existingVote = voteRepository.findByPostIdAndUserId(voteDTO.getPostId(), userId);

        if (existingVote.isPresent()) {
            return updateVote(existingVote.get().getId(), voteDTO);
        }

        VoteType voteType = findVoteTypeById(voteDTO.getVoteTypeId());

        Vote vote = new Vote();
        vote.setPost(postService.getPostEntityById(voteDTO.getPostId()));
        vote.setUser(userService.getUserEntityById(userId));
        vote.setVoteType(voteType);
        vote.setValue(valueFor(voteType)); // FIX: value was never set -> NOT NULL violation
        vote.setCreationDate(LocalDateTime.now());

        Vote savedVote = voteRepository.save(vote);

        // Update post score
        if (UP.equals(voteType.getName())) {
            postService.incrementScore(voteDTO.getPostId(), 1);
            userService.incrementUpVotes(userId);
        } else if (DOWN.equals(voteType.getName())) {
            postService.incrementScore(voteDTO.getPostId(), -1);
            userService.incrementDownVotes(userId);
        }

        notificationService.create(
                savedVote.getPost().getOwner(),
                savedVote.getUser(),
                NotificationType.POST_VOTE,
                "New vote on your post",
                savedVote.getUser().getDisplayName() + " voted on \"" + savedVote.getPost().getTitle() + "\"",
                "/posts/" + savedVote.getPost().getId());

        return mapToDTO(savedVote);
    }

    public Integer createUpvote(Integer postId, Integer userId) {
        return castVote(postId, userId, UP);
    }

    public Integer createDownvote(Integer postId, Integer userId) {
        return castVote(postId, userId, DOWN);
    }

    public VoteResponse getVoteById(Integer voteId) {
        return mapToDTO(findVoteById(voteId));
    }

    public VoteResponse updateVote(Integer voteId, VoteRequest voteDTO) {
        Vote vote = findVoteById(voteId);
        VoteType newVoteType = findVoteTypeById(voteDTO.getVoteTypeId());
        return mapToDTO(changeVoteType(vote, newVoteType));
    }

    public void deleteVote(Integer voteId) {
        Vote vote = findVoteById(voteId);

        // Adjust post score
        if (UP.equals(vote.getVoteType().getName())) {
            postService.incrementScore(vote.getPost().getId(), -1);
        } else if (DOWN.equals(vote.getVoteType().getName())) {
            postService.incrementScore(vote.getPost().getId(), 1);
        }

        voteRepository.deleteById(voteId);
    }

    public List<VoteResponse> getVotesByPostId(Integer postId) {
        return voteRepository.findByPostId(postId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    /**
     * Shared logic for createUpvote/createDownvote: creates the vote,
     * or switches the type if the user already voted on this post.
     */
    private Integer castVote(Integer postId, Integer userId, String voteTypeName) {
        VoteType voteType = voteTypeRepository.findAll().stream()
                .filter(t -> voteTypeName.equals(t.getName()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("VoteType not found with name: " + voteTypeName));

        Optional<Vote> existingVote = voteRepository.findByPostIdAndUserId(postId, userId);
        if (existingVote.isPresent()) {
            return changeVoteType(existingVote.get(), voteType).getId();
        }

        Vote vote = new Vote();
        vote.setPost(postService.getPostEntityById(postId));
        vote.setUser(userService.getUserEntityById(userId));
        vote.setVoteType(voteType);
        vote.setValue(valueFor(voteType));
        vote.setCreationDate(LocalDateTime.now());
        Vote saved = voteRepository.save(vote);

        if (UP.equals(voteTypeName)) {
            postService.incrementScore(postId, 1);
            userService.incrementUpVotes(userId);
        } else {
            postService.incrementScore(postId, -1);
            userService.incrementDownVotes(userId);
        }

        return saved.getId();
    }

    /**
     * Changes the vote type, keeps value in sync, and adjusts the post score.
     */
    private Vote changeVoteType(Vote vote, VoteType newVoteType) {
        String oldName = vote.getVoteType().getName();
        String newName = newVoteType.getName();

        vote.setVoteType(newVoteType);
        vote.setValue(valueFor(newVoteType)); // FIX: keep value in sync on update

        if (UP.equals(oldName) && DOWN.equals(newName)) {
            postService.incrementScore(vote.getPost().getId(), -2); // -1 old upvote, -1 new downvote
        } else if (DOWN.equals(oldName) && UP.equals(newName)) {
            postService.incrementScore(vote.getPost().getId(), 2); // +1 remove downvote, +1 new upvote
        }

        return voteRepository.save(vote);
    }

    /**
     * Derives the stored value from the vote type.
     * NOTE: change the return type if Vote.value is not an Integer.
     */
    private Integer valueFor(VoteType voteType) {
        if (UP.equals(voteType.getName())) return 1;
        if (DOWN.equals(voteType.getName())) return -1;
        return 0;
    }

    private Vote findVoteById(Integer voteId) {
        return voteRepository.findById(voteId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote not found with id: " + voteId));
    }

    private VoteType findVoteTypeById(Integer voteTypeId) {
        return voteTypeRepository.findById(voteTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("VoteType not found with id: " + voteTypeId));
    }

    private VoteResponse mapToDTO(Vote vote) {
        return new VoteResponse(
                vote.getId(),
                vote.getPost().getId(),
                vote.getVoteType().getId(),
                vote.getValue(),
                vote.getUser().getId(),
                vote.getCreationDate()
        );
    }
}