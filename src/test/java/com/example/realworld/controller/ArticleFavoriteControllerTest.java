package com.example.realworld.controller;

import com.example.realworld.entity.User;
import com.example.realworld.security.CustomUserDetails;
import com.example.realworld.security.JwtService;
import com.example.realworld.service.ArticleFavoriteService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleFavoriteController.class)
@AutoConfigureMockMvc(addFilters = false)
class ArticleFavoriteControllerTest {

    @MockitoBean
    private ArticleFavoriteService articleFavoriteService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("arthur@example.com")
                .username("arthur")
                .password("pass")
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        var authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testFavoriteArticleSuccessfully() throws Exception {
        doNothing().when(articleFavoriteService)
                .favoriteArticleBySlug(any(User.class), eq("how-to-train-your-dragon"));

        mockMvc.perform(post("/api/article/how-to-train-your-dragon/favorite"))
                .andExpect(status().isCreated());

        verify(articleFavoriteService)
                .favoriteArticleBySlug(any(User.class), eq("how-to-train-your-dragon"));
    }

    @Test
    void testUnfavoriteArticleSuccessfully() throws Exception {
        doNothing().when(articleFavoriteService)
                .unfavoriteArticleBySlug(any(User.class), eq("how-to-train-your-dragon"));

        mockMvc.perform(delete("/api/article/how-to-train-your-dragon/favorite"))
                .andExpect(status().isNoContent());

        verify(articleFavoriteService)
                .unfavoriteArticleBySlug(any(User.class), eq("how-to-train-your-dragon"));
    }
}
