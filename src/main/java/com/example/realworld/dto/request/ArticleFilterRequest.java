package com.example.realworld.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleFilterRequest {
    private String tag;
    private String author;
    private String favorited;
    private int limit = 20;
    private int offset = 10;
}
