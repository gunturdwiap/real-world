package com.example.realworld.controller;

import com.example.realworld.security.CustomUserDetails;
import com.example.realworld.service.ArticleFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/article/{slug}/favorite")
@RequiredArgsConstructor
public class ArticleFavoriteController {
    private final ArticleFavoriteService articleFavoriteService;

    @PostMapping
    public ResponseEntity<Object> store(@PathVariable String slug,
                                        @AuthenticationPrincipal CustomUserDetails userDetails){
        articleFavoriteService.favoriteArticleBySlug(userDetails.getUser(), slug);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Object> delete(@PathVariable String slug,
                                        @AuthenticationPrincipal CustomUserDetails userDetails){
        articleFavoriteService.unfavoriteArticleBySlug(userDetails.getUser(), slug);

        return ResponseEntity.noContent().build();
    }
}
