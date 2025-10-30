package com.example.realworld.service;

import com.example.realworld.dto.response.ProfileDTO;
import com.example.realworld.dto.response.ProfileResponse;
import com.example.realworld.entity.Follow;
import com.example.realworld.entity.User;
import com.example.realworld.repository.FollowRepository;
import com.example.realworld.repository.UserRepository;
import com.example.realworld.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    public ProfileResponse findProfile(User currentUser, String username) {
        User targetUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        boolean isFollowing = false;
        if (currentUser != null && !currentUser.getId().equals(targetUser.getId())) {
            isFollowing = followRepository.existsByFollowerAndFollowee(currentUser, targetUser);
        }

        return new ProfileResponse(ProfileDTO.builder()
                .username(targetUser.getUsername())
                .image(targetUser.getImage())
                .bio(targetUser.getBio())
                .following(isFollowing)
                .build());
    }

    public ProfileResponse follow(User currentUser, String username){
        User targetUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (currentUser.getId().equals(targetUser.getId())) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        if (!followRepository.existsByFollowerAndFollowee(currentUser, targetUser)) {
            followRepository.save(Follow.builder()
                            .follower(currentUser)
                            .followee(targetUser)
                            .build());
        }

        return new ProfileResponse(ProfileDTO.builder()
                .username(targetUser.getUsername())
                .image(targetUser.getImage())
                .bio(targetUser.getBio())
                .following(true)
                .build());
    }

    public ProfileResponse unfollow(User currentUser, String username){
        User targetUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        followRepository.deleteByFollowerAndFollowee(currentUser, targetUser);

        return new ProfileResponse(ProfileDTO.builder()
                .username(targetUser.getUsername())
                .image(targetUser.getImage())
                .bio(targetUser.getBio())
                .following(false)
                .build());
    }

}
