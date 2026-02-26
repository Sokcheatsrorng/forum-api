package com.forum.repository;

import com.forum.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    Optional<Tag> findByTagName(String tagName);

    boolean existsByTagName(String tagName);

    @Query("SELECT t FROM Tag t ORDER BY t.count DESC")
    List<Tag> findPopularTags();

    @Query("SELECT t FROM Tag t WHERE t.tagName LIKE %:searchTerm%")
    List<Tag> searchTags(@Param("searchTerm") String searchTerm);

    @Query(value = "SELECT t.* FROM tags t ORDER BY t.count DESC LIMIT :limit", nativeQuery = true)
    List<Tag> findTopTags(@Param("limit") Integer limit);
}
