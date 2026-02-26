package com.forum.repository;

import com.forum.entity.PostHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostHistoryRepository extends JpaRepository<PostHistory, Integer> {

    List<PostHistory> findByPostId(Integer postId);

    List<PostHistory> findByUserId(Integer userId);

    List<PostHistory> findByPostIdOrderByCreationDateDesc(Integer postId);
}
