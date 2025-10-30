package com.example.realworld.controller;

import com.example.realworld.dto.request.StoreCommentRequest;
import com.example.realworld.dto.response.CommentResponse;
import com.example.realworld.dto.response.MultipleCommentResponse;
import com.example.realworld.entity.User;
import com.example.realworld.security.CustomUserDetails;
import com.example.realworld.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/articles/{slug}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<MultipleCommentResponse> index(@PathVariable String slug,
                                                         @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = Optional.ofNullable(userDetails)
                .map(CustomUserDetails::getUser)
                .orElse(null);

        MultipleCommentResponse response = commentService.findAllByArticleSlug(user, slug);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CommentResponse> store(@PathVariable String slug,
                                                 @Valid @RequestBody StoreCommentRequest request,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails){

        CommentResponse response = commentService.storeByArticleSlug(userDetails.getUser(), slug, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable String slug,
                                         @PathVariable UUID id,
                                         @AuthenticationPrincipal CustomUserDetails userDetails){

        commentService.delete(userDetails.getUser(), slug, id);

        return ResponseEntity.noContent().build();
    }
}
