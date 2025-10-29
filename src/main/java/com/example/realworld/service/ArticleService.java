package com.example.realworld.service;

import com.example.realworld.dto.request.ArticleFilterRequest;
import com.example.realworld.dto.request.StoreArticleRequest;
import com.example.realworld.dto.request.UpdateArticleRequest;
import com.example.realworld.dto.response.ArticleDTO;
import com.example.realworld.dto.response.ArticleResponse;
import com.example.realworld.dto.response.MultipleArticleResponse;
import com.example.realworld.entity.Article;
import com.example.realworld.entity.Tag;
import com.example.realworld.entity.User;
import com.example.realworld.repository.ArticleFavoriteRepository;
import com.example.realworld.repository.ArticleRepository;
import com.example.realworld.repository.FollowRepository;
import com.example.realworld.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.realworld.specification.ArticleSpecifications.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final FollowRepository followRepository;
    private final ArticleFavoriteRepository articleFavoriteRepository;
    private final TagRepository tagRepository;

    public MultipleArticleResponse findAll(User user, ArticleFilterRequest request) {
        Pageable pageable = PageRequest.of(request.getOffset() / request.getLimit(), request.getLimit());

        Page<Article> articlesPage = articleRepository.findAll(
                hasTag(request.getTag())
                        .and(hasAuthor(request.getAuthor()))
                        .and(isFavoritedBy(request.getFavorited())),
                pageable
        );

        if (articlesPage.isEmpty()) {
            return new MultipleArticleResponse(List.of(), 0);
        }

        return buildMultipleArticleResponse(articlesPage.getContent(), user, articlesPage.getTotalElements());
    }

    public MultipleArticleResponse findFeedForUser(User user, Pageable pageable) {
        List<User> followedAuthors = followRepository.findFolloweesByFollower(user);
        if (followedAuthors.isEmpty()) {
            return new MultipleArticleResponse(List.of(), 0);
        }

        Page<Article> articlesPage = articleRepository.findByAuthorInOrderByCreatedAtDesc(followedAuthors, pageable);

        return buildMultipleArticleResponse(articlesPage.getContent(), user, articlesPage.getTotalElements());
    }

    public ArticleResponse findArticleBySlug(User currentUser, String slug) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));

        boolean favorited = currentUser != null
                && articleFavoriteRepository.existsByUserAndArticle(currentUser, article);

        boolean isFollowingAuthor = currentUser != null
                && followRepository.existsByFollowerAndFollowee(currentUser, article.getAuthor());

        int favoritesCount = articleFavoriteRepository.countByArticle(article);

        ArticleDTO articleDTO = ArticleDTO.fromEntity(article, isFollowingAuthor, favorited, favoritesCount);

        return new ArticleResponse(articleDTO);
    }

    public ArticleResponse store(User user, StoreArticleRequest request) {
        Set<Tag> tags = getOrCreateTags(request.getArticle().getTagList());

        Article article = articleRepository.save(Article
                .builder()
                .author(user)
                .title(request.getArticle().getTitle())
                .slug(slugify(request.getArticle().getTitle()))
                .description(request.getArticle().getDescription())
                .body(request.getArticle().getBody())
                .tags(tags)
                .build());

        ArticleDTO articleDTO = ArticleDTO.fromEntity(article, false, false, 0);

        return new ArticleResponse(articleDTO);
    }


    public ArticleResponse update(User user, String slug, UpdateArticleRequest request) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));

        if (!article.getAuthorId().equals(user.getId())) {
            throw new AccessDeniedException("Forbidden");
        }

        article.setTitle(request.getArticle().getTitle().isEmpty() ? article.getTitle() : request.getArticle().getTitle());
        article.setSlug(request.getArticle().getTitle().isEmpty() ? article.getSlug() : slugify(request.getArticle().getTitle()));
        article.setDescription(request.getArticle().getDescription().isEmpty() ? article.getDescription() : request.getArticle().getDescription());
        article.setBody(request.getArticle().getBody().isEmpty() ? article.getBody() : request.getArticle().getBody());

        article = articleRepository.save(article);

        boolean favorited = articleFavoriteRepository.existsByUserAndArticle(user, article);

        int favoritesCount = articleFavoriteRepository.countByArticle(article);

        ArticleDTO articleDTO = ArticleDTO.fromEntity(article, false, favorited, favoritesCount);

        return new ArticleResponse(articleDTO);
    }

    public void delete(User user, String slug) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));

        if (!article.getAuthorId().equals(user.getId())) {
            throw new AccessDeniedException("Forbidden");
        }

        articleRepository.delete(article);
    }

    private String slugify(String string){
        return string
                .toLowerCase() // Convert to lowercase
                .replaceAll("[^a-z0-9]+", "-") // Replace non-alphanumeric characters with hyphens
                .replaceAll("^-+", "") // Remove leading hyphens
                .replaceAll("-+$", "") // Remove trailing hyphens
                .replaceAll("--", "-"); // Remove duplicate hyphens

    }

    private Set<Tag> getOrCreateTags(Set<String> tagNames){
        if (tagNames == null || tagNames.isEmpty()){
            return Collections.emptySet();
        }

        return tagNames.stream()
                .map(tag -> tagRepository.findByName(tag)
                        .orElseGet(() -> tagRepository.save(new Tag(tag))))
                .collect(Collectors.toSet());
    }

    private MultipleArticleResponse buildMultipleArticleResponse(List<Article> articles, User user, long totalElements) {
        List<UUID> articleIds = articles.stream().map(Article::getId).toList();
        List<UUID> authorIds = articles.stream()
                .map(a -> a.getAuthor().getId())
                .distinct()
                .toList();

        Set<UUID> favoritedArticleIds = user != null
                ? new HashSet<>(articleFavoriteRepository.findFavoritedArticleIds(user, articleIds))
                : Collections.emptySet();

        Map<UUID, Integer> favoritesCountMap = articleFavoriteRepository.countFavoritesByArticleIds(articleIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (UUID) row[0],
                        row -> ((Long) row[1]).intValue()
                ));

        Set<UUID> followedAuthorIds = user != null
                ? new HashSet<>(followRepository.findFollowedAuthorIds(user, authorIds))
                : Collections.emptySet();

        List<ArticleDTO> articleDTOs = articles.stream()
                .map(article -> {
                    boolean favorited = favoritedArticleIds.contains(article.getId());
                    int favoritesCount = favoritesCountMap.getOrDefault(article.getId(), 0);
                    boolean following = followedAuthorIds.contains(article.getAuthor().getId());
                    return ArticleDTO.fromEntity(article, following, favorited, favoritesCount);
                })
                .toList();

        return new MultipleArticleResponse(articleDTOs, (int) totalElements);
    }
}
