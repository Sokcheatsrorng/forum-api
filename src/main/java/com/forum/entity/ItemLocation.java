package com.forum.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "location")
@Getter @Setter @NoArgsConstructor
public class ItemLocation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 100) private String building;
    @Column(length = 20) private String floor;
    @Column(length = 20) private String room;
}
