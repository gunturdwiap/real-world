package com.example.realworld.repository;

import com.example.realworld.entity.Article;
import com.example.realworld.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleRepository extends JpaRepository<Article, UUID>, JpaSpecificationExecutor<Article> {
    @EntityGraph(attributePaths = {"author"})
    Optional<Article> findBySlug(String slug);

    Page<Article> findByAuthorInOrderByCreatedAtDesc(List<User> followedAuthors, Pageable pageable);
}
