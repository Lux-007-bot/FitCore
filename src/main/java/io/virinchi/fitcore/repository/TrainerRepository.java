package io.virinchi.fitcore.repository;

import io.virinchi.fitcore.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
}