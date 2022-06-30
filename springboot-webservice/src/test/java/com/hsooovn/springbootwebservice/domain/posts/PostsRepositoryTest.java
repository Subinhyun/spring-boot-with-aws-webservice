package com.hsooovn.springbootwebservice.domain.posts;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostsRepositoryTest {
    @Autowired
    PostRepository postRepository;

    @After
    public void cleanup() {
        postRepository.deleteAll();
    }

    @Test
    public void saveAndFindAll() {
        String title = "test title";
        String content = "test content";

        postRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author("hkhh1029@naver.com")
                .build());

        List<Posts> postsList = postRepository.findAll();

        Posts posts = postsList.get(0);
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);
    }
}
