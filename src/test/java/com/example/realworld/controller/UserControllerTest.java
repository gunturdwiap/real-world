package com.example.realworld.controller;

import com.example.realworld.dto.request.UpdateUserRequest;
import com.example.realworld.dto.response.UserDTO;
import com.example.realworld.dto.response.UserResponse;
import com.example.realworld.entity.User;
import com.example.realworld.security.CustomUserDetails;
import com.example.realworld.security.JwtService;
import com.example.realworld.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomUserDetails currentUser;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        currentUser = new CustomUserDetails(
                User.builder()
                        .id(UUID.randomUUID())
                        .email("arthurmorgan@example.com")
                        .username("arthur")
                        .password("plaintext")
                        .image("https://i.imgur.com/arthur.png")
                        .bio("no")
                        .build()
        );

        updateRequest = new UpdateUserRequest(
                "johnmarston@example.com",
                "john",
                "verystrongpassword",
                "https://i.imgur.com/john.png",
                "yes"
        );

        var authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown(){
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetUserSuccessfully() throws Exception {
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value(currentUser.getUser().getEmail()))
                .andExpect(jsonPath("$.user.username").value(currentUser.getUser().getUsername()))
                .andExpect(jsonPath("$.user.bio").value(currentUser.getUser().getBio()))
                .andExpect(jsonPath("$.user.image").value(currentUser.getUser().getImage()));
    }

    @Test
    void testUpdateUserSuccessfully() throws Exception {
        var mockResponse = new UserResponse(
                UserDTO.builder()
                        .email(updateRequest.getEmail())
                        .username(updateRequest.getUsername())
                        .bio(updateRequest.getBio())
                        .image(updateRequest.getImage())
                        .build()
        );

        when(userService.update(currentUser.getUser(), updateRequest))
                .thenReturn(mockResponse);

        mockMvc.perform(put("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value(updateRequest.getEmail()))
                .andExpect(jsonPath("$.user.username").value(updateRequest.getUsername()))
                .andExpect(jsonPath("$.user.bio").value(updateRequest.getBio()))
                .andExpect(jsonPath("$.user.image").value(updateRequest.getImage()));
    }
}