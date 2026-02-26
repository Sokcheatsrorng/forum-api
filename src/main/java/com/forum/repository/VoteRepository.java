package com.forum.repository;

import com.forum.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer> {

    List<Vote> findByPostId(Integer postId);


    @Query("SELECT v FROM Vote v WHERE v.post.id = :postId AND v.user.id = :userId")
    Optional<Vote> findByPostIdAndUserId(@Param("postId") Integer postId, @Param("userId") Integer userId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.post.id = :postId AND v.voteType.name = 'UpMod'")
    Integer countUpVotes(@Param("postId") Integer postId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.post.id = :postId AND v.voteType.name = 'DownMod'")
    Integer countDownVotes(@Param("postId") Integer postId);

    @Query("SELECT v FROM Vote v WHERE v.post.id = :postId ORDER BY v.creationDate DESC")
    List<Vote> findByPostIdOrderByRecent(@Param("postId") Integer postId);
}
