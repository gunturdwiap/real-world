package com.example.realworld.controller;

import com.example.realworld.dto.response.TagListResponse;
import com.example.realworld.security.JwtService;
import com.example.realworld.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false)
class TagControllerTest {

    @MockitoBean
    private TagService tagService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetTagsSuccessfully() throws Exception {
        var mockResponse = new TagListResponse(List.of("reactjs", "angularjs", "dragons"));
        when(tagService.findAll()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/tags").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags[0]").value("reactjs"))
                .andExpect(jsonPath("$.tags[1]").value("angularjs"))
                .andExpect(jsonPath("$.tags[2]").value("dragons"));
    }
}
