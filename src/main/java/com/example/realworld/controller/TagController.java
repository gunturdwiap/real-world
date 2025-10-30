package com.example.realworld.controller;

import com.example.realworld.dto.response.TagListResponse;
import com.example.realworld.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping
    public ResponseEntity<TagListResponse> index(){
        TagListResponse response = tagService.findAll();

        return ResponseEntity.ok(response);
    }
}
