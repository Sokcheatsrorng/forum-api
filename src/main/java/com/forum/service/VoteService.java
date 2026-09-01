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

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class VoteService {

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

        Vote vote = new Vote();
        vote.setPost(postService.getPostEntityById(voteDTO.getPostId()));
        vote.setUser(userService.getUserEntityById(userId));

        VoteType voteType = voteTypeRepository.findById(voteDTO.getVoteTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("VoteType not found with id: " + voteDTO.getVoteTypeId()));
        vote.setVoteType(voteType);

        vote.setCreationDate(LocalDateTime.now());

        Vote savedVote = voteRepository.save(vote);

        // Update post score
        if ("UpMod".equals(voteType.getName())) {
            postService.incrementScore(voteDTO.getPostId(), 1);
            userService.incrementUpVotes(userId);
        } else if ("DownMod".equals(voteType.getName())) {
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

        Optional<Vote> existingVote = voteRepository.findByPostIdAndUserId(postId, userId);

        if (existingVote.isPresent()) {
            voteRepository.countUpVotes(existingVote.isPresent() ? existingVote.get().getPost().getId() : postId);
        }

        Vote vote = new Vote();

        vote.setPost(postService.getPostEntityById(postId));

        vote.setUser(userService.getUserEntityById(userId));

        return voteRepository.save(vote).getId();
    }

    public Integer createDownvote(Integer postId, Integer userId) {
        Optional<Vote> existingVote = voteRepository.findByPostIdAndUserId(postId, userId);

        if (existingVote.isPresent()) {
            voteRepository.countUpVotes(existingVote.isPresent() ? existingVote.get().getPost().getId() : postId);
        }

        Vote vote = new Vote();

        vote.setPost(postService.getPostEntityById(postId));

        vote.setUser(userService.getUserEntityById(userId));

        return voteRepository.save(vote).getId();

    }

    public VoteResponse getVoteById(Integer voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote not found with id: " + voteId));
        return mapToDTO(vote);
    }

    public VoteResponse updateVote(Integer voteId, VoteRequest voteDTO) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote not found with id: " + voteId));

        VoteType newVoteType = voteTypeRepository.findById(voteDTO.getVoteTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("VoteType not found with id: " + voteDTO.getVoteTypeId()));

        String oldVoteType = vote.getVoteType().getName();
        vote.setVoteType(newVoteType);

        // Adjust post score based on vote type change
        if ("UpMod".equals(oldVoteType) && "DownMod".equals(newVoteType.getName())) {
            postService.incrementScore(vote.getPost().getId(), -2); // -1 for old upvote, -1 for new downvote
        } else if ("DownMod".equals(oldVoteType) && "UpMod".equals(newVoteType.getName())) {
            postService.incrementScore(vote.getPost().getId(), 2); // +1 for removing downvote, +1 for new upvote
        }

        Vote updatedVote = voteRepository.save(vote);
        return mapToDTO(updatedVote);
    }

    public void deleteVote(Integer voteId) {
        Vote vote = voteRepository.findById(voteId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote not found with id: " + voteId));

        // Adjust post score
        if ("UpMod".equals(vote.getVoteType().getName())) {
            postService.incrementScore(vote.getPost().getId(), -1);
        } else if ("DownMod".equals(vote.getVoteType().getName())) {
            postService.incrementScore(vote.getPost().getId(), 1);
        }

        voteRepository.deleteById(voteId);
    }

    public List<VoteResponse> getVotesByPostId(Integer postId) {
        return voteRepository.findByPostId(postId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

//    public List<VoteResponse> getVotesByUserId(Integer userId) {
//        return voteRepository.findByUserId(userId).stream()
//                .map(this::mapToDTO)
//                .collect(Collectors.toList());
//    }

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
