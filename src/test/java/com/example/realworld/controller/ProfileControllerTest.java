package com.example.realworld.controller;

import com.example.realworld.dto.response.ProfileDTO;
import com.example.realworld.dto.response.ProfileResponse;
import com.example.realworld.entity.User;
import com.example.realworld.security.CustomUserDetails;
import com.example.realworld.security.JwtService;
import com.example.realworld.service.ProfileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private ProfileService profileService;

    private final String BASE_URL = "/api/profiles/javier";

    @BeforeEach
    void beforeEach(){
        var currentUser = new CustomUserDetails(
                User.builder()
                        .id(UUID.randomUUID())
                        .email("arthurmorgan@example.com")
                        .username("arthur")
                        .password("plaintext")
                        .image("https://i.imgur.com/arthur.png")
                        .bio("no")
                        .build()
        );

        var authentication = new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown(){
        SecurityContextHolder.clearContext();
    }


    private ProfileResponse buildMockResponse(boolean following) {
        return new ProfileResponse(ProfileDTO.builder()
                .username("javier")
                .bio("indeed")
                .image("https://api.realworld.io/images/smiley-cyrus.jpg")
                .following(following)
                .build());
    }

    @Test
    void testGetProfileSuccessfully() throws Exception {
        ProfileResponse mockResponse = buildMockResponse(false);
        Mockito.when(profileService.findProfile(any(User.class), eq("javier")))
                .thenReturn(mockResponse);

        mockMvc.perform(get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.profile.username").value("javier"))
                .andExpect(jsonPath("$.profile.bio").value("indeed"))
                .andExpect(jsonPath("$.profile.image").value("https://api.realworld.io/images/smiley-cyrus.jpg"))
                .andExpect(jsonPath("$.profile.following").value(false));
    }

    @Test
    @WithMockUser
    void testFollowSuccessfully() throws Exception {
        ProfileResponse mockResponse = buildMockResponse(true);
        Mockito.when(profileService.follow(any(User.class), eq("javier")))
                .thenReturn(mockResponse);

        mockMvc.perform(post(BASE_URL + "/follow")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.username").value("javier"))
                .andExpect(jsonPath("$.profile.following").value(true));
    }

    @Test
    @WithMockUser
    void testUnfollowSuccessfully() throws Exception {
        ProfileResponse mockResponse = buildMockResponse(false);
        Mockito.when(profileService.unfollow(any(User.class), eq("javier")))
                .thenReturn(mockResponse);

        mockMvc.perform(delete(BASE_URL + "/follow")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.username").value("javier"))
                .andExpect(jsonPath("$.profile.following").value(false));
    }
}
