package io.virinchi.fitcore.repository;

import io.virinchi.fitcore.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogPostRepository extends JpaRepository<BlogPost, Integer> {

    List<BlogPost> findAllByOrderByCreatedAtDesc();
}