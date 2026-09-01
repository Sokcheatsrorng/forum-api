package com.forum.repository;
import com.forum.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Integer> { Optional<ItemCategory> findByNameIgnoreCase(String name); }
