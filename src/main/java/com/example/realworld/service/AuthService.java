package com.example.realworld.service;

import com.example.realworld.dto.request.LoginRequest;
import com.example.realworld.dto.request.RegisterRequest;
import com.example.realworld.dto.response.UserDTO;
import com.example.realworld.dto.response.UserResponse;
import com.example.realworld.entity.User;
import com.example.realworld.repository.UserRepository;
import com.example.realworld.security.CustomUserDetails;
import com.example.realworld.security.JwtService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private  final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse register(RegisterRequest registerRequest){
        User existingUser = userRepository.findByEmail(registerRequest.getEmail())
                .orElse(null);

        if (existingUser != null) throw new ValidationException("User already exist");

        User user = userRepository.save(User
                .builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .build()
        );

        return new UserResponse(UserDTO.fromEntity(user));
    }

    public UserResponse login(LoginRequest loginRequest){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));

        User user = userRepository.findByEmail(loginRequest.getUsername())
                .orElseThrow();

        String token = jwtService.generateToken(new CustomUserDetails(user));

        UserDTO dto = UserDTO.fromEntity(user);

        dto.setToken(token);

        return new UserResponse(dto);
    }


}
