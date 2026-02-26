package com.forum.repository;

import com.forum.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    // ── Single post (full load) ──────────────────────────────────────────────
    @EntityGraph(attributePaths = {"tags", "comments", "comments.user", "owner", "postType"})
    @Query("SELECT p FROM Post p WHERE p.id = :id")
    Optional<Post> findFullById(@Param("id") Integer id);

    // ── All posts ────────────────────────────────────────────────────────────
    @EntityGraph(attributePaths = {"tags", "comments", "comments.user", "owner", "postType"})
    @Query("SELECT DISTINCT p FROM Post p ORDER BY p.creationDate DESC")
    List<Post> findAllOrderByNewest();

    @EntityGraph(attributePaths = {"tags", "comments", "comments.user", "owner", "postType"})
    @Query("SELECT DISTINCT p FROM Post p ORDER BY p.score DESC")
    List<Post> findAllOrderByScore();

    @EntityGraph(attributePaths = {"tags", "comments", "comments.user", "owner", "postType"})
    @Query("SELECT DISTINCT p FROM Post p ORDER BY p.viewCount DESC")
    List<Post> findAllOrderByViews();

    // ── Filter by owner ──────────────────────────────────────────────────────
    @EntityGraph(attributePaths = {"tags", "comments", "comments.user", "owner", "postType"})
    @Query("SELECT DISTINCT p FROM Post p WHERE p.owner.id = :ownerId ORDER BY p.creationDate DESC")
    List<Post> findByOwnerId(@Param("ownerId") Integer ownerId);

    // ── Filter by post type ──────────────────────────────────────────────────
    @EntityGraph(attributePaths = {"tags", "comments", "comments.user", "owner", "postType"})
    @Query("SELECT DISTINCT p FROM Post p WHERE p.postType.id = :postTypeId ORDER BY p.creationDate DESC")
    List<Post> findByPostTypeId(@Param("postTypeId") Integer postTypeId);

    // ── Filter by tag ────────────────────────────────────────────────────────
    @EntityGraph(attributePaths = {"tags", "comments", "comments.user", "owner", "postType"})
    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE t.id = :tagId ORDER BY p.creationDate DESC")
    List<Post> findByTagId(@Param("tagId") Integer tagId);

    // ── Answers (by parentId) ────────────────────────────────────────────────
    @EntityGraph(attributePaths = {"tags", "comments", "comments.user", "owner", "postType"})
    @Query("SELECT DISTINCT p FROM Post p WHERE p.parentId = :parentId ORDER BY p.score DESC")
    List<Post> findByParentId(@Param("parentId") Integer parentId);

    // ── Search ───────────────────────────────────────────────────────────────
    @EntityGraph(attributePaths = {"tags", "comments", "comments.user", "owner", "postType"})
    @Query("SELECT DISTINCT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.body) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY p.creationDate DESC")
    List<Post> searchPosts(@Param("searchTerm") String searchTerm);

    @EntityGraph(attributePaths = {"tags", "comments", "comments.user", "owner", "postType"})
    @Query("SELECT DISTINCT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.body) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY p.score DESC, p.viewCount DESC")
    List<Post> searchPostsByRelevance(@Param("searchTerm") String searchTerm);

}