package com.example.realworld.controller;

import com.example.realworld.dto.request.StoreCommentRequest;
import com.example.realworld.dto.response.CommentDTO;
import com.example.realworld.dto.response.CommentResponse;
import com.example.realworld.dto.response.MultipleCommentResponse;
import com.example.realworld.dto.response.ProfileDTO;
import com.example.realworld.entity.User;
import com.example.realworld.security.CustomUserDetails;
import com.example.realworld.security.JwtService;
import com.example.realworld.service.CommentService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentControllerTest {

    @MockitoBean
    private CommentService commentService;

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

    private ProfileDTO author() {
        return ProfileDTO.builder()
                .username("jake")
                .bio("I work at statefarm")
                .image("https://api.realworld.io/images/smiley-cyrus.jpg")
                .following(false)
                .build();
    }

    private CommentDTO buildComment(UUID id, String body) {
        return CommentDTO.builder()
                .id(id)
                .createdAt(LocalDateTime.parse("2016-02-18T03:22:56.637"))
                .updatedAt(LocalDateTime.parse("2016-02-18T03:22:56.637"))
                .body(body)
                .author(author())
                .build();
    }

    @Test
    void testGetCommentsSuccessfully() throws Exception {
        var c1 = buildComment(UUID.randomUUID(), "Nice article!");
        var c2 = buildComment(UUID.randomUUID(), "Great insights!");
        var response = new MultipleCommentResponse(List.of(c1, c2));

        when(commentService.findAllByArticleSlug(any(), eq("how-to-train-your-dragon")))
                .thenReturn(response);

        mockMvc.perform(get("/api/articles/how-to-train-your-dragon/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments[0].body").value("Nice article!"))
                .andExpect(jsonPath("$.comments[1].body").value("Great insights!"));
    }

    @Test
    void testStoreCommentSuccessfully() throws Exception {
        var requestJson = """
            {
              "body": "Great article!"
            }
            """;

        var commentDTO = buildComment(UUID.randomUUID(), "Great article!");
        var response = new CommentResponse(commentDTO);

        when(commentService.storeByArticleSlug(any(User.class), eq("how-to-train-your-dragon"), any(StoreCommentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/articles/how-to-train-your-dragon/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment.body").value("Great article!"))
                .andExpect(jsonPath("$.comment.author.username").value("jake"));
    }

    @Test
    void testDeleteCommentSuccessfully() throws Exception {
        UUID commentId = UUID.randomUUID();
        doNothing().when(commentService).delete(any(User.class), eq("how-to-train-your-dragon"), eq(commentId));

        mockMvc.perform(delete("/api/articles/how-to-train-your-dragon/comments/" + commentId))
                .andExpect(status().isNoContent());

        verify(commentService).delete(any(User.class), eq("how-to-train-your-dragon"), eq(commentId));
    }
}
