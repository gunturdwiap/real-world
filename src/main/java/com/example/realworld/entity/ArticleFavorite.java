package com.example.realworld.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "articles_users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleFavorite {
    @EmbeddedId
    private ArticleFavoriteId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("articleId")
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;
}

