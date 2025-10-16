package com.example.realworld.controller;

import com.example.realworld.dto.response.ProfileResponse;
import com.example.realworld.security.CustomUserDetails;
import com.example.realworld.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles/{username}")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal CustomUserDetails currentUser, @PathVariable String username){
        ProfileResponse response = profileService.findProfile(username, currentUser);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/follow")
    public ResponseEntity<ProfileResponse> follow(@AuthenticationPrincipal CustomUserDetails currentUser, @PathVariable String username){
        ProfileResponse response = profileService.follow(username, currentUser);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/follow")
    public ResponseEntity<ProfileResponse> unfollow(@AuthenticationPrincipal CustomUserDetails currentUser, @PathVariable String username){
        ProfileResponse response = profileService.unfollow(username, currentUser);

        return ResponseEntity.ok(response);
    }
}
