package com.forum.bookmark;


import com.forum.entity.BookMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookMarkRepository extends JpaRepository<BookMark, Long> {


    Optional<BookMark> findByUserId(Integer userId);

//    boolean existsByUserIdAndPostsId(String userId, String postId);

}
