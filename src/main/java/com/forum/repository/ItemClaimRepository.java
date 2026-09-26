package com.forum.repository;
import com.forum.entity.ItemClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ItemClaimRepository extends JpaRepository<ItemClaim, Integer> { List<ItemClaim> findByItemReportIdOrderByCreatedAtDesc(Integer itemReportId);

    boolean existsByItemReportIdAndStatus(Integer reportId, String approved);

    void deleteByItemReportId(Integer reportId);
}
