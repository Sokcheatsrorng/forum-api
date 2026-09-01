package com.forum.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim")
@Getter @Setter @NoArgsConstructor
public class ItemClaim {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "item_report_id", nullable = false) private ItemReport itemReport;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "claimant_user_id", nullable = false) private User claimant;
    @Column(length = 500) private String describedHiddenDetail;
    @Column(nullable = false, length = 10) private String status = "pending";
    @Column(nullable = false) private Integer failedAttemptCount = 0;
    @Column(nullable = false) private boolean confirmedByFinder = false;
    @Column(nullable = false) private boolean confirmedByClaimant = false;
    private LocalDateTime returnedAt;
    private LocalDateTime reviewedAt;
    @Column(nullable = false) private LocalDateTime createdAt = LocalDateTime.now();
}
