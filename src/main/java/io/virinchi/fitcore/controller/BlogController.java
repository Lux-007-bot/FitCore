package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.BlogPost;
import io.virinchi.fitcore.service.BlogPostService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class BlogController {

    private final BlogPostService blogPostService;

    public BlogController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    // =========================================================
    // PUBLIC
    // =========================================================

    @GetMapping("/blog")
    public String blogPage(Model model) {

        model.addAttribute(
                "posts",
                blogPostService.getAllPosts()
        );

        return "blog";
    }

    @GetMapping("/blog/{id}/image")
    @ResponseBody
    public byte[] getPostImage(@PathVariable Integer id) {

        BlogPost post =
                blogPostService.getPostById(id).orElse(null);

        if (post == null || post.getImageData() == null) {
            return new byte[0];
        }

        return post.getImageData();
    }

    // =========================================================
    // ADMIN
    // =========================================================

    @GetMapping("/admin/blog")
    public String manageBlogPosts(
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "posts",
                blogPostService.getAllPosts()
        );

        return "admin/blog-posts";
    }

    @GetMapping("/admin/blog/new")
    public String newBlogPost(
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "post",
                new BlogPost()
        );

        return "admin/add-blog-post";
    }

    @PostMapping("/admin/blog/new")
    public String createBlogPost(
            @ModelAttribute BlogPost post,
            @RequestParam("imageFile") MultipartFile imageFile,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        if (imageFile != null && !imageFile.isEmpty()) {

            try {
                post.setImageData(imageFile.getBytes());
                post.setImageType(imageFile.getContentType());

            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/admin/blog/new";
            }
        }

        blogPostService.savePost(post);

        return "redirect:/admin/blog";
    }

    @GetMapping("/admin/blog/{id}/edit")
    public String editBlogPost(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        BlogPost post =
                blogPostService.getPostById(id).orElse(null);

        if (post == null) {
            return "redirect:/admin/blog";
        }

        model.addAttribute("post", post);

        return "admin/edit-blog-post";
    }

    @PostMapping("/admin/blog/{id}/edit")
    public String updateBlogPost(
            @PathVariable Integer id,
            @ModelAttribute BlogPost post,
            @RequestParam("imageFile") MultipartFile imageFile,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        BlogPost existingPost =
                blogPostService.getPostById(id).orElse(null);

        if (existingPost == null) {
            return "redirect:/admin/blog";
        }

        existingPost.setTitle(post.getTitle());
        existingPost.setContent(post.getContent());

        if (imageFile != null && !imageFile.isEmpty()) {

            try {
                existingPost.setImageData(imageFile.getBytes());
                existingPost.setImageType(imageFile.getContentType());

            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/admin/blog/" + id + "/edit";
            }
        }

        blogPostService.savePost(existingPost);

        return "redirect:/admin/blog";
    }

    @PostMapping("/admin/blog/{id}/delete")
    public String deleteBlogPost(
            @PathVariable Integer id,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        blogPostService.deletePost(id);

        return "redirect:/admin/blog";
    }
}