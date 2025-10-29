package com.example.realworld.controller;

import com.example.realworld.dto.request.ArticleFilterRequest;
import com.example.realworld.dto.request.StoreArticleRequest;
import com.example.realworld.dto.request.UpdateArticleRequest;
import com.example.realworld.dto.response.ArticleResponse;
import com.example.realworld.dto.response.MultipleArticleResponse;
import com.example.realworld.entity.User;
import com.example.realworld.security.CustomUserDetails;
import com.example.realworld.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<MultipleArticleResponse> index(@ModelAttribute ArticleFilterRequest articleFilterRequest,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails){
        User currentUser = Optional.ofNullable(userDetails)
                .map(CustomUserDetails::getUser)
                .orElse(null);

        MultipleArticleResponse response = articleService.findAll(currentUser, articleFilterRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/feed")
    public  ResponseEntity<MultipleArticleResponse> feed(@RequestParam("limit") Integer limit,
                                                 @RequestParam("offset") Integer offset,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        Pageable pageable = PageRequest.of(offset/limit, limit);

        MultipleArticleResponse response = articleService.findFeedForUser(userDetails.getUser(), pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ArticleResponse> show(@PathVariable String slug,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        User currentUser = Optional.ofNullable(userDetails)
                .map(CustomUserDetails::getUser)
                .orElse(null);

        ArticleResponse article = articleService.findArticleBySlug(currentUser, slug);

        return ResponseEntity.ok(article);
    }

    @PostMapping
    public ResponseEntity<ArticleResponse> store(@RequestBody StoreArticleRequest request,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        ArticleResponse response = articleService.store(userDetails.getUser(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{slug}")
    public ResponseEntity<ArticleResponse> update(@PathVariable String slug,
                                                  @RequestBody UpdateArticleRequest request,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails){

        ArticleResponse response = articleService.update(userDetails.getUser(), slug, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<Object> delete(@PathVariable String slug,
                                         @AuthenticationPrincipal CustomUserDetails userDetails){
        articleService.delete(userDetails.getUser(), slug);

        return ResponseEntity.noContent().build();
    }

}
