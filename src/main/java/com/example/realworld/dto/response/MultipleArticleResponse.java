package com.example.realworld.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipleArticleResponse {
    private List<ArticleDTO> articles;
    private long articlesCount;
}

