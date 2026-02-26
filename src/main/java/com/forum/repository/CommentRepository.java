package com.forum.repository;

import com.forum.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByPostId(Integer postId);

    List<Comment> findByUserId(Integer userId);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.score DESC")
    List<Comment> findByPostIdOrderByScore(@Param("postId") Integer postId);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.creationDate DESC")
    List<Comment> findByPostIdOrderByRecent(@Param("postId") Integer postId);

    @Query("SELECT c FROM Comment c WHERE c.text LIKE %:searchTerm%")
    List<Comment> searchComments(@Param("searchTerm") String searchTerm);
}
