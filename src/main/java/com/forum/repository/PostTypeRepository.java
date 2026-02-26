package com.forum.repository;

import com.forum.entity.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PostTypeRepository extends JpaRepository<PostType, Integer> {

    Optional<PostType> findByName(String name);
}
