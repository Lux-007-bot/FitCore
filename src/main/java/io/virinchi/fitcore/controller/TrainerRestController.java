package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.Trainer;
import io.virinchi.fitcore.service.TrainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trainers")
public class TrainerRestController {

    private final TrainerService trainerService;

    public TrainerRestController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping
    public ResponseEntity<List<Trainer>> getAllTrainers() {
        List<Trainer> trainers = trainerService.getAllTrainers();
        return ResponseEntity.ok(trainers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trainer> getTrainerById(@PathVariable Integer id) {
        Optional<Trainer> trainer = trainerService.getTrainerById(id);
        return trainer
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createTrainer(@RequestBody Trainer trainer) {
        if (trainer.getName() == null || trainer.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Trainer name is required.");
        }
        if (trainer.getEmail() == null || trainer.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Trainer email is required.");
        }

        trainer.setId(null);
        Trainer saved = trainerService.saveTrainer(trainer);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrainer(@PathVariable Integer id, @RequestBody Trainer trainer) {
        Optional<Trainer> existingOpt = trainerService.getTrainerById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Trainer existing = existingOpt.get();
        existing.setName(trainer.getName());
        existing.setEmail(trainer.getEmail());
        existing.setPhone(trainer.getPhone());
        existing.setSpecialization(trainer.getSpecialization());
        existing.setExperienceYears(trainer.getExperienceYears());
        existing.setDescription(trainer.getDescription());

        Trainer updated = trainerService.saveTrainer(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrainer(@PathVariable Integer id) {
        Optional<Trainer> existing = trainerService.getTrainerById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        trainerService.deleteTrainer(id);
        return ResponseEntity.noContent().build();
    }
}