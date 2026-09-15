package io.virinchi.fitcore.service;

import io.virinchi.fitcore.model.GalleryImage;
import io.virinchi.fitcore.repository.GalleryImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GalleryImageService {

    private final GalleryImageRepository galleryImageRepository;

    public GalleryImageService(
            GalleryImageRepository galleryImageRepository) {

        this.galleryImageRepository = galleryImageRepository;
    }

    public List<GalleryImage> getAllImages() {
        return galleryImageRepository.findAll();
    }

    public Optional<GalleryImage> getImageById(Integer id) {
        return galleryImageRepository.findById(id);
    }

    public GalleryImage saveImage(GalleryImage galleryImage) {
        return galleryImageRepository.save(galleryImage);
    }

    public void deleteImage(Integer id) {
        galleryImageRepository.deleteById(id);
    }
}