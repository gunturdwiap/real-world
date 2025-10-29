package com.example.realworld.repository;

import com.example.realworld.entity.Article;
import com.example.realworld.entity.ArticleFavorite;
import com.example.realworld.entity.ArticleFavoriteId;
import com.example.realworld.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ArticleFavoriteRepository extends JpaRepository<ArticleFavorite, ArticleFavoriteId> {
    boolean existsByUserAndArticle(User user, Article article);
    void deleteByUserAndArticle(User user, Article article);
    int countByArticle(Article article);

    @Query("SELECT af.article.id FROM ArticleFavorite af " +
            "WHERE af.user = :user AND af.article.id IN :articleIds")
    List<UUID> findFavoritedArticleIds(@Param("user") User user, @Param("articleIds") List<UUID> articleIds);

    @Query("SELECT af.article.id AS articleId, COUNT(af.user.id) AS count " +
            "FROM ArticleFavorite af WHERE af.article.id IN :articleIds GROUP BY af.article.id")
    List<Object[]> countFavoritesByArticleIds(@Param("articleIds") List<UUID> articleIds);
}

