package com.example.realworld.controller;

import com.example.realworld.dto.request.UpdateUserRequest;
import com.example.realworld.dto.response.UserDTO;
import com.example.realworld.dto.response.UserResponse;
import com.example.realworld.entity.User;
import com.example.realworld.security.CustomUserDetails;
import com.example.realworld.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomUserDetails createMockUserDetails() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("john@example.com");
        user.setPassword("password");
        user.setUsername("john");
        user.setBio("Initial bio");
        user.setImage("https://image.url");

        return new CustomUserDetails(user);
    }

    @Test
    void shouldReturnCurrentUserProfile() throws Exception {
        CustomUserDetails mockUserDetails = createMockUserDetails();

        UserDTO userDTO = UserDTO.builder()
                .email("john@example.com")
                .username("john")
                .bio("Initial bio")
                .image("https://image.url")
                .token("fake-jwt-token")
                .build();

        UserResponse mockResponse = new UserResponse(userDTO);

        when(userService.findByEmail("john@example.com")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/user")
                        .principal(mockUserDetails))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("john@example.com"))
                .andExpect(jsonPath("$.user.username").value("john"))
                .andExpect(jsonPath("$.user.bio").value("Initial bio"))
                .andExpect(jsonPath("$.user.image").value("https://image.url"))
                .andExpect(jsonPath("$.user.token").value("fake-jwt-token"));
    }

    @Test
    void shouldUpdateCurrentUserProfile() throws Exception {
        CustomUserDetails mockUserDetails = createMockUserDetails();

        UpdateUserRequest updateRequest = UpdateUserRequest.builder()
                .username("john_updated")
                .email("john.updated@example.com")
                .bio("Updated bio")
                .image("https://new-image.url")
                .build();

        UserDTO updatedDTO = UserDTO.builder()
                .username("john_updated")
                .email("john.updated@example.com")
                .bio("Updated bio")
                .image("https://new-image.url")
                .token("new-jwt-token")
                .build();

        UserResponse updatedResponse = new UserResponse(updatedDTO);

        when(userService.update(Mockito.eq(1L), any(UpdateUserRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/api/user")
                        .principal(mockUserDetails)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("john_updated"))
                .andExpect(jsonPath("$.user.email").value("john.updated@example.com"))
                .andExpect(jsonPath("$.user.bio").value("Updated bio"))
                .andExpect(jsonPath("$.user.image").value("https://new-image.url"))
                .andExpect(jsonPath("$.user.token").value("new-jwt-token"));
    }
}
