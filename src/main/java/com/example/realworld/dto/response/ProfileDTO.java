package com.example.realworld.dto.response;

import com.example.realworld.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {
    private String username;
    private String bio;
    private String image;
    private boolean following;

    public static ProfileDTO fromEntity(User user, boolean isFollowing){
        return builder()
                .username(user.getUsername())
                .image(user.getImage())
                .bio(user.getBio())
                .following(isFollowing)
                .build();
    }
}
