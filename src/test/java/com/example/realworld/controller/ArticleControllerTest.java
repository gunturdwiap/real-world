package com.example.realworld.controller;

import com.example.realworld.dto.request.ArticleFilterRequest;
import com.example.realworld.dto.request.StoreArticleRequest;
import com.example.realworld.dto.request.UpdateArticleRequest;
import com.example.realworld.dto.response.ArticleDTO;
import com.example.realworld.dto.response.ArticleResponse;
import com.example.realworld.dto.response.MultipleArticleResponse;
import com.example.realworld.dto.response.ProfileDTO;
import com.example.realworld.entity.User;
import com.example.realworld.security.CustomUserDetails;
import com.example.realworld.security.JwtService;
import com.example.realworld.service.ArticleService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArticleController.class)
@AutoConfigureMockMvc(addFilters = false)
class ArticleControllerTest {

    @MockitoBean
    private ArticleService articleService;

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
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private ProfileDTO author() {
        return ProfileDTO.builder()
                .username("jake")
                .bio("I work at statefarm")
                .image("https://i.stack.imgur.com/xHWG8.jpg")
                .following(false)
                .build();
    }

    private ArticleDTO buildArticle(String slug, String title, String desc) {
        return ArticleDTO.builder()
                .slug(slug)
                .title(title)
                .description(desc)
                .body("Some body text")
                .tagList(Set.of("dragons", "training"))
                .createdAt(LocalDateTime.parse("2016-02-18T03:22:56.637"))
                .updatedAt(LocalDateTime.parse("2016-02-18T03:48:35.824"))
                .favorited(false)
                .favoritesCount(0)
                .author(author())
                .build();
    }

    @Test
    void testGetArticlesSuccessfully() throws Exception {
        var article1 = buildArticle("slug-1", "Title 1", "Desc 1");
        var article2 = buildArticle("slug-2", "Title 2", "Desc 2");
        var mockResponse = new MultipleArticleResponse(List.of(article1, article2), 2);

        when(articleService.findAll(any(User.class), any(ArticleFilterRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/articles")
                        .param("limit", "10")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(2))
                .andExpect(jsonPath("$.articles[0].slug").value("slug-1"))
                .andExpect(jsonPath("$.articles[1].slug").value("slug-2"));
    }

    @Test
    void testGetFeedSuccessfully() throws Exception {
        var article = buildArticle("feed-slug", "Feed Title", "Feed Desc");
        var response = new MultipleArticleResponse(List.of(article), 1);

        when(articleService.findFeedForUser(any(User.class), any()))
                .thenReturn(response);

        mockMvc.perform(get("/api/articles/feed")
                        .param("limit", "5")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(1))
                .andExpect(jsonPath("$.articles[0].slug").value("feed-slug"));
    }

    @Test
    void testGetArticleBySlug() throws Exception {
        var articleDTO = buildArticle("my-article", "My Title", "Some description");
        var response = new ArticleResponse(articleDTO);

        when(articleService.findArticleBySlug(any(), eq("my-article")))
                .thenReturn(response);

        mockMvc.perform(get("/api/articles/my-article"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.slug").value("my-article"))
                .andExpect(jsonPath("$.article.title").value("My Title"));
    }

    @Test
    void testStoreArticleSuccessfully() throws Exception {
        var requestJson = """
            {
              "title": "New Article",
              "description": "About dragons",
              "body": "Dragon content here",
              "tagList": ["dragons", "training"]
            }
            """;

        var articleDTO = buildArticle("new-article", "New Article", "About dragons");
        var response = new ArticleResponse(articleDTO);

        when(articleService.store(any(User.class), any(StoreArticleRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.article.slug").value("new-article"))
                .andExpect(jsonPath("$.article.title").value("New Article"));
    }

    @Test
    void testUpdateArticleSuccessfully() throws Exception {
        var requestJson = """
            {
                "title": "Updated Title",
                "body": "Updated body text"
            }
            """;

        var updatedArticle = buildArticle("existing-slug", "Updated Title", "Desc");
        var response = new ArticleResponse(updatedArticle);

        when(articleService.update(any(User.class), eq("existing-slug"), any(UpdateArticleRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/articles/existing-slug")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.title").value("Updated Title"));
    }

    // 🧪 DELETE /api/articles/{slug}
    @Test
    void testDeleteArticleSuccessfully() throws Exception {
        doNothing().when(articleService).delete(any(User.class), eq("slug-to-delete"));

        mockMvc.perform(delete("/api/articles/slug-to-delete"))
                .andExpect(status().isNoContent());

        verify(articleService).delete(any(User.class), eq("slug-to-delete"));
    }
}
