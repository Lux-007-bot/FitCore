package io.virinchi.fitcore.service;

import io.virinchi.fitcore.model.BlogPost;
import io.virinchi.fitcore.repository.BlogPostRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;

    public BlogPostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    public List<BlogPost> getAllPosts() {
        return blogPostRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<BlogPost> getPostById(Integer id) {
        return blogPostRepository.findById(id);
    }

    public BlogPost savePost(BlogPost post) {

        if (post.getCreatedAt() == null) {
            post.setCreatedAt(LocalDateTime.now());
        }

        return blogPostRepository.save(post);
    }

    public void deletePost(Integer id) {
        blogPostRepository.deleteById(id);
    }
}