package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hashtags")
public class Hashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "hashtag_seq")
    @SequenceGenerator(name = "hashtag_seq",
            sequenceName = "hashtag_seq",
            allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "hashtag")
    private List<PostHashtag> postHashtags = new ArrayList<>();

    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<PostHashtag> getPostHashtags() { return postHashtags; }

    public void setPostHashtags(List<PostHashtag> postHashtags) { this.postHashtags = postHashtags; }
}