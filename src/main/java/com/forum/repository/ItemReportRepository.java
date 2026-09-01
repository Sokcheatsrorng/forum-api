package com.forum.repository;
import com.forum.entity.ItemReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ItemReportRepository extends JpaRepository<ItemReport, Integer> {
    List<ItemReport> findByStatusOrderByCreatedAtDesc(String status);
    List<ItemReport> findByItemTypeAndStatusOrderByCreatedAtDesc(String itemType, String status);
    List<ItemReport> findByCategoryIdAndItemTypeAndStatus(Integer categoryId, String itemType, String status);
}
