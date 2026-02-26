package com.forum.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String tagName;

    @Column(nullable = true)
    private Integer count = 0;

    @Column(nullable = true, length = 500)
    private String excerptPostId;

    @Column(nullable = true)
    private Integer wikiPostId;


    @JsonIgnore  // ← add this
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
// ← remove cascade = CascadeType.ALL from the inverse side, it doesn't belong here
    private Set<Post> posts = new HashSet<>();
}
