package com.example.realworld.dto.request;

import com.example.realworld.entity.Article;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateArticleRequest {
    private ArticleData article;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleData {
        @Size(max = 255)
        private String title;
        private String description;
        private String body;
    }
}
