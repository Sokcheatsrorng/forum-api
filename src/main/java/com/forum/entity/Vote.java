package com.forum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "votes")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_type_id", nullable = false)
    private VoteType voteType;

//    private Integer upVote;
//
//    private Integer downVote;

    @Column(nullable = false)
    private Integer value;

    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    private String indexHint;
}
