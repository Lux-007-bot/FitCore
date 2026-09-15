package io.virinchi.fitcore.repository;

import io.virinchi.fitcore.model.GalleryImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalleryImageRepository extends JpaRepository<GalleryImage, Integer> {
}