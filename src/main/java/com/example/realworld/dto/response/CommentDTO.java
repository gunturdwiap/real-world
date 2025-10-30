package com.example.realworld.dto.response;

import com.example.realworld.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CommentDTO {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String body;
    private ProfileDTO author;

    public static CommentDTO fromEntity(Comment comment, boolean isFollowingAuthor){
        return CommentDTO.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .body(comment.getBody())
                .author(ProfileDTO.fromEntity(comment.getAuthor(), isFollowingAuthor))
                .build();
    }
}
