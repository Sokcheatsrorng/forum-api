package com.forum.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "match", uniqueConstraints = @UniqueConstraint(columnNames = {"lost_item_id", "found_item_id"}))
@Getter @Setter @NoArgsConstructor
public class ItemMatch {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "lost_item_id", nullable = false) private ItemReport lostItem;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "found_item_id", nullable = false) private ItemReport foundItem;
    @Column(precision = 4, scale = 3) private BigDecimal categoryScore;
    @Column(precision = 4, scale = 3) private BigDecimal dateScore;
    @Column(precision = 4, scale = 3) private BigDecimal locationScore;
    @Column(precision = 4, scale = 3) private BigDecimal keywordScore;
    @Column(precision = 4, scale = 3) private BigDecimal totalScore;
    @Column(nullable = false, length = 10) private String status = "suggested";
    @Column(nullable = false) private LocalDateTime createdAt = LocalDateTime.now();
}
