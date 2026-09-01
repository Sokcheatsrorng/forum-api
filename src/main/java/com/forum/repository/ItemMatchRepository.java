package com.forum.repository;
import com.forum.entity.ItemMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ItemMatchRepository extends JpaRepository<ItemMatch, Integer> {
    boolean existsByLostItemIdAndFoundItemId(Integer lostItemId, Integer foundItemId);
    List<ItemMatch> findByLostItemIdOrFoundItemIdOrderByTotalScoreDesc(Integer lostItemId, Integer foundItemId);
}
