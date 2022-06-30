package com.hsooovn.springbootwebservice.service.posts;

import com.hsooovn.springbootwebservice.domain.posts.Posts;
import com.hsooovn.springbootwebservice.domain.posts.PostsRepository;
import com.hsooovn.springbootwebservice.web.dto.PostsResponseDto;
import com.hsooovn.springbootwebservice.web.dto.PostsSaveRequestsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postRepository;

    @Transactional
    public Long save(PostsSaveRequestsDto requestsDto) {
        return postRepository.save(requestsDto.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, PostsSaveRequestsDto requestsDto) {
        Posts posts = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        posts.update(requestsDto.getTitle(), requestsDto.getContent());
        return id;
    }

    @Transactional
    public PostsResponseDto findById(Long id) {
        Posts entity = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        return new PostsResponseDto(entity);
    }
}
