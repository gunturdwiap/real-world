package com.example.realworld.controller;

import com.example.realworld.dto.response.ProfileResponse;
import com.example.realworld.entity.User;
import com.example.realworld.security.CustomUserDetails;
import com.example.realworld.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/profiles/{username}")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal CustomUserDetails currentUser, @PathVariable String username){
        User user = Optional.ofNullable(currentUser)
                .map(CustomUserDetails::getUser)
                .orElse(null);

        ProfileResponse response = profileService.findProfile(user, username);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/follow")
    public ResponseEntity<ProfileResponse> follow(@AuthenticationPrincipal CustomUserDetails currentUser, @PathVariable String username){
        ProfileResponse response = profileService.follow(currentUser.getUser(), username);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/follow")
    public ResponseEntity<ProfileResponse> unfollow(@AuthenticationPrincipal CustomUserDetails currentUser, @PathVariable String username){
        ProfileResponse response = profileService.unfollow(currentUser.getUser(), username);

        return ResponseEntity.ok(response);
    }
}
