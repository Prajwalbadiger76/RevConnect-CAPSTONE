package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "post_views",
       uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
public class PostView {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "post_view_seq")
    @SequenceGenerator(name = "post_view_seq",
            sequenceName = "post_view_seq",
            allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}