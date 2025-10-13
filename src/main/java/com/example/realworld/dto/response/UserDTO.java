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
public class UserDTO {
    private String email;
    private String token;
    private String username;
    private String bio;
    private String image;

    public static UserDTO fromEntity(User user) {
        return builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .bio(user.getBio())
                .image(user.getImage())
                .build();
    }

}

