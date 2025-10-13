package com.example.realworld.controller;

import com.example.realworld.dto.request.LoginRequest;
import com.example.realworld.dto.request.RegisterRequest;
import com.example.realworld.dto.response.UserDTO;
import com.example.realworld.dto.response.UserResponse;
import com.example.realworld.security.JwtService;
import com.example.realworld.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest("user@example.com", "username", "password123");
        UserDTO userDTO = UserDTO.builder()
                .email("user@example.com")
                .username("username")
                .build();
        UserResponse response = new UserResponse(userDTO);
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.username").value("username"))
                .andExpect(jsonPath("$.user.email").value("user@example.com"));
    }

    @Test
    void testLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest("user@example.com", "password123");

        UserDTO userDTO = new UserDTO("user@example.com", "token456", "username", "bio", "image.png");
        UserResponse expectedResponse = new UserResponse(userDTO);

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("username"))
                .andExpect(jsonPath("$.user.token").value("token456"))
                .andExpect(jsonPath("$.user.email").value("user@example.com"));

    }
}
