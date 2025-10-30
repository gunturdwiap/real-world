package com.example.realworld.controller;

import com.example.realworld.dto.request.UpdateUserRequest;
import com.example.realworld.dto.response.UserDTO;
import com.example.realworld.dto.response.UserResponse;
import com.example.realworld.security.CustomUserDetails;
import com.example.realworld.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> get(@AuthenticationPrincipal CustomUserDetails userDetails){
        UserDTO userDTO = UserDTO.fromEntity(userDetails.getUser());

        return ResponseEntity.ok(new UserResponse(userDTO));
    }

    @PutMapping
    public ResponseEntity<UserResponse> update(@AuthenticationPrincipal CustomUserDetails currentUser,
                                               @Valid @RequestBody UpdateUserRequest updateUserRequest){
        UserResponse userResponse = userService.update(currentUser.getUser(), updateUserRequest);

        return ResponseEntity.ok(userResponse);
    }
}
