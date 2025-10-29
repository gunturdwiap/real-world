package com.example.realworld.dto.response;

import com.example.realworld.entity.Article;
import com.example.realworld.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDTO {
    private String slug;
    private String title;
    private String description;
    private String body;
    private Set<String> tagList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProfileDTO author;
    private boolean favorited;
    private int favoritesCount;

    public static ArticleDTO fromEntity(Article article, boolean isFollowing, boolean isFavorited, int favoritesCount){
        return builder()
                .slug(article.getSlug())
                .title(article.getTitle())
                .description(article.getDescription())
                .body(article.getBody())
                .tagList(article.getTags().stream().map(Tag::getName).collect(Collectors.toSet()))
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .favorited(isFavorited)
                .favoritesCount(favoritesCount)
                .author(ProfileDTO.fromEntity(article.getAuthor(), isFollowing))
                .build();
    }
}
