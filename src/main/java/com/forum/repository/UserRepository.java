package com.forum.repository;

import com.forum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);


    @Query("""
            SELECT DISTINCT u FROM User u
            LEFT JOIN FETCH u.posts
            LEFT JOIN FETCH u.comments
            LEFT JOIN FETCH u.bookmark b
            LEFT JOIN FETCH b.posts
            WHERE u.email = :email
            """)
    Optional<User> findByEmailWithDetails(@Param("email") String email);

    Optional<User> findByDisplayName(String displayName);

    boolean existsByEmail(String email);

    boolean existsByDisplayName(String displayName);

    @Query("SELECT u FROM User u WHERE u.reputation > :minReputation ORDER BY u.reputation DESC")
    List<User> findTopUsersByReputation(@Param("minReputation") Integer minReputation);

    @Query("SELECT u FROM User u WHERE u.displayName LIKE %:searchTerm% OR u.email LIKE %:searchTerm%")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);
}
