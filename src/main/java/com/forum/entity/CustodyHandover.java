package com.forum.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "custody_handover")
@Getter @Setter @NoArgsConstructor
public class CustodyHandover {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "item_report_id", unique = true, nullable = false) private ItemReport itemReport;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "staff_user_id", nullable = false) private User staff;
    @Column(nullable = false, length = 10) private String status = "held";
    @Column(length = 255) private String claimantIdentityNote;
    private LocalDateTime heldAt;
    private LocalDateTime collectedAt;
}
