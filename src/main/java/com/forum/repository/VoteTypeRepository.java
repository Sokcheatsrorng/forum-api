package com.forum.repository;

import com.forum.entity.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VoteTypeRepository extends JpaRepository<VoteType, Integer> {

    Optional<VoteType> findByName(String name);
}
