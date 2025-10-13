package com.example.realworld.service;

import com.example.realworld.dto.request.UpdateUserRequest;
import com.example.realworld.dto.response.UserDTO;
import com.example.realworld.dto.response.UserResponse;
import com.example.realworld.entity.User;
import com.example.realworld.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse findByEmail(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        return new UserResponse(UserDTO.fromEntity(user));
    }

    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow();

        user.setBio(request.getBio().isEmpty() ? user.getBio() : request.getBio());
        user.setEmail(request.getEmail().isEmpty() ? user.getEmail() : request.getEmail());
        user.setImage(request.getImage().isEmpty() ? user.getImage() : request.getImage());
        user.setPassword(request.getPassword().isEmpty() ? user.getPassword() : passwordEncoder.encode(request.getPassword()));

        user = userRepository.save(user);

        return new UserResponse(UserDTO.fromEntity(user));
    }
}
