package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.GalleryImage;
import io.virinchi.fitcore.service.GalleryImageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class GalleryController {

    private final GalleryImageService galleryImageService;

    public GalleryController(GalleryImageService galleryImageService) {
        this.galleryImageService = galleryImageService;
    }

    @GetMapping("/gallery")
    public String gallery(Model model) {
        model.addAttribute(
                "galleryImages",
                galleryImageService.getAllImages()
        );

        return "gallery";
    }

    @GetMapping("/gallery/image/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable Integer id) {

        GalleryImage image =
                galleryImageService.getImageById(id).orElse(null);

        if (image == null || image.getImageData() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getImageType()))
                .body(image.getImageData());
    }

    @GetMapping("/admin/gallery")
    public String adminGallery(
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "galleryImages",
                galleryImageService.getAllImages()
        );

        return "admin/gallery";
    }

    @PostMapping("/admin/gallery/upload")
    public String uploadImage(
            @RequestParam String title,
            @RequestParam MultipartFile image,
            HttpSession session) throws IOException {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        GalleryImage galleryImage = new GalleryImage();

        galleryImage.setTitle(title);
        galleryImage.setImageData(image.getBytes());
        galleryImage.setImageType(image.getContentType());

        galleryImageService.saveImage(galleryImage);

        return "redirect:/admin/gallery";
    }

    @PostMapping("/admin/gallery/{id}/delete")
    public String deleteImage(
            @PathVariable Integer id,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        galleryImageService.deleteImage(id);

        return "redirect:/admin/gallery";
    }
}