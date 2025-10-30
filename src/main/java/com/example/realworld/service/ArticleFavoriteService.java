package com.example.realworld.service;

import com.example.realworld.entity.Article;
import com.example.realworld.entity.ArticleFavorite;
import com.example.realworld.entity.User;
import com.example.realworld.repository.ArticleFavoriteRepository;
import com.example.realworld.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleFavoriteService {
    private final ArticleRepository articleRepository;
    private final ArticleFavoriteRepository articleFavoriteRepository;

    public void favoriteArticleBySlug(User user, String slug) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));

        boolean alreadyFavorited = articleFavoriteRepository.existsByArticleAndUser(article, user);
        if (alreadyFavorited) {
            return;
        }

        articleFavoriteRepository.save(ArticleFavorite.builder()
                .article(article)
                .user(user)
                .build());
    }

    public void unfavoriteArticleBySlug(User user, String slug) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));

        ArticleFavorite favorite = articleFavoriteRepository.findByArticleAndUser(article, user)
                .orElseThrow(() -> new EntityNotFoundException("Favorite not found"));

        articleFavoriteRepository.delete(favorite);
    }
}
