package com.forum.service;

import com.forum.dto.CommentRequest;
import com.forum.dto.CommentResponse;
import com.forum.dto.VoteResponse;
import com.forum.entity.Comment;
import com.forum.entity.Post;
import com.forum.entity.Vote;
import com.forum.exception.ResourceNotFoundException;
import com.forum.repository.CommentRepository;
import com.forum.repository.VoteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;
    private final VoteRepository voteRepository;

    public CommentResponse createComment(CommentRequest commentDTO, Integer userId) {
        Comment comment = new Comment();
        comment.setPost(postService.getPostEntityById(commentDTO.getPostId()));
        comment.setUser(userService.getUserEntityById(userId));
        comment.setText(commentDTO.getText());
        comment.setCreationDate(LocalDateTime.now());
        comment.setLastEditDate(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return mapToDTO(savedComment);
    }

    public CommentResponse getCommentById(Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        return mapToDTO(comment);
    }

    public CommentResponse updateComment(Integer commentId, CommentRequest commentDTO, Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Only the comment owner can update the comment");
        }

        comment.setText(commentDTO.getText());
        comment.setLastEditDate(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        return mapToDTO(updatedComment);
    }


    public void deleteComment(Integer commentId, Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Only the comment owner can delete the comment");
        }

        commentRepository.deleteById(commentId);
    }

    public List<CommentResponse> getCommentsByPostId(Integer postId) {
        return commentRepository.findByPostIdOrderByRecent(postId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CommentResponse> getCommentsByUserId(Integer userId) {
        return commentRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CommentResponse> searchComments(String searchTerm) {
        return commentRepository.searchComments(searchTerm).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void incrementScore(Integer commentId, Integer points) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        comment.setScore(comment.getScore() + points);
        commentRepository.save(comment);
    }

    private CommentResponse mapToDTO(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getText(),
                comment.getScore(),
                comment.getUser().getId(),
                comment.getUser().getDisplayName(),
                comment.getCreationDate(),
                comment.getLastEditDate()
        );
    }
}
