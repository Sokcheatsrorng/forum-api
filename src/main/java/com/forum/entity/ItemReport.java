package com.forum.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_report")
@Getter @Setter @NoArgsConstructor
public class ItemReport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "user_id") private User user;
    @Column(nullable = false, length = 10) private String itemType;
    @Column(nullable = false) private String title;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "category_id") private ItemCategory category;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(nullable = false) private LocalDate itemDate;
    @Column(nullable = false, length = 10) private String scope;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "location_id") private ItemLocation location;
    @Column(precision = 9, scale = 6) private BigDecimal mapLat;
    @Column(precision = 9, scale = 6) private BigDecimal mapLng;
    @Column(length = 255) private String freeTextLocation;
    @Column(length = 500) private String photoUrl;
    @Column(length = 500) private String hiddenDetail;
    @Column(nullable = false, length = 25) private String status = "open";
    @Column(nullable = false, length = 10) private String moderationStatus = "pending";
    @Column(nullable = false) private Integer flagCount = 0;
    private LocalDate expiryDate;
    @Column(nullable = false) private boolean extended = false;
    @Column(length = 10) private String archivedDisposition;
    @Column(nullable = false) private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable = false) private LocalDateTime updatedAt = LocalDateTime.now();
    @PreUpdate void updateTimestamp() { updatedAt = LocalDateTime.now(); }
}
