package com.example.realworld.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreArticleRequest {
    private ArticleData article;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleData{
        @NotBlank
        @Size(max = 255)
        private String title;

        @NotBlank
        private String description;

        @NotBlank
        private String body;

        private Set<String> tagList;
    }
}
