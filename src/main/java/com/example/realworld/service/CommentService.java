package com.example.realworld.service;

import com.example.realworld.dto.request.StoreCommentRequest;
import com.example.realworld.dto.response.CommentDTO;
import com.example.realworld.dto.response.CommentResponse;
import com.example.realworld.dto.response.MultipleCommentResponse;
import com.example.realworld.entity.Article;
import com.example.realworld.entity.Comment;
import com.example.realworld.entity.User;
import com.example.realworld.repository.ArticleRepository;
import com.example.realworld.repository.CommentRepository;
import com.example.realworld.repository.FollowRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final ArticleRepository articleRepository;
    private final FollowRepository followRepository;
    private final CommentRepository commentRepository;

    public MultipleCommentResponse findAllByArticleSlug(User currentUser, String slug){
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));

        List<Comment> comments = article.getComments();

        List<UUID> commentAuthorIds = comments.stream()
                .map(Comment::getAuthorId)
                .distinct()
                .toList();

        Set<UUID> followedAuthorIds = currentUser != null
                ? new HashSet<>(followRepository.findFollowedAuthorIds(currentUser, commentAuthorIds))
                : Collections.emptySet();

        List<CommentDTO> commentDTOs = comments.stream()
                .map(comment -> {
                    boolean isFollowing = followedAuthorIds.contains(comment.getAuthorId());
                    return CommentDTO.fromEntity(comment, isFollowing);
                })
                .toList();

        return new MultipleCommentResponse(commentDTOs);
    }


    public CommentResponse storeByArticleSlug(User user, String slug, StoreCommentRequest request) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));

        Comment comment = commentRepository.save(Comment
                .builder()
                .article(article)
                .author(user)
                .body(request.getBody())
                .build());

        CommentDTO commentDTO = CommentDTO.fromEntity(comment, false);

        return new CommentResponse(commentDTO);
    }

    public void delete(User user, String slug, UUID id) {
        articleRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (!comment.getAuthorId().equals(user.getId())){
            throw new AccessDeniedException("Forbidden");
        }

        commentRepository.delete(comment);
    }
}
