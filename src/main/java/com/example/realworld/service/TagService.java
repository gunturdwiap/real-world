package com.example.realworld.service;

import com.example.realworld.dto.response.TagListResponse;
import com.example.realworld.entity.Tag;
import com.example.realworld.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public TagListResponse findAll(){
        List<String> tags = tagRepository.findAll().stream()
                .map(Tag::getName)
                .toList();

        return new TagListResponse(tags);
    }
}
