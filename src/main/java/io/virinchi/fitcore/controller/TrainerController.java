package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.Trainer;
import io.virinchi.fitcore.service.TrainerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    // =========================
    // PUBLIC TRAINER PROFILE
    // =========================

    @GetMapping("/trainer/{id}")
    public String viewTrainerProfile(
            @PathVariable Integer id,
            Model model) {

        Trainer trainer =
                trainerService.getTrainerById(id).orElse(null);

        if (trainer == null) {
            return "redirect:/";
        }

        model.addAttribute("trainer", trainer);

        return "trainer-profile";
    }


    // =========================
    // ADMIN - MANAGE TRAINERS
    // =========================

    @GetMapping("/admin/trainers")
    public String manageTrainers(
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "trainers",
                trainerService.getAllTrainers()
        );

        return "admin/trainers";
    }


    // =========================
    // ADMIN - ADD TRAINER PAGE
    // =========================

    @GetMapping("/admin/trainers/new")
    public String newTrainer(
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "trainer",
                new Trainer()
        );

        return "admin/add-trainer";
    }


    // =========================
    // ADMIN - CREATE TRAINER
    // =========================

    @PostMapping("/admin/trainers/new")
    public String createTrainer(
            @ModelAttribute Trainer trainer,
            @RequestParam("imageFile") MultipartFile imageFile,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        // Upload image
        if (imageFile != null && !imageFile.isEmpty()) {

            try {

                String fileName =
                        imageFile.getOriginalFilename();

                if (fileName != null && !fileName.isBlank()) {

                    Path uploadPath = Paths.get(
                            "src/main/resources/static/assets"
                    );

                    Files.createDirectories(uploadPath);

                    Path filePath =
                            uploadPath.resolve(fileName);

                    Files.copy(
                            imageFile.getInputStream(),
                            filePath,
                            StandardCopyOption.REPLACE_EXISTING
                    );

                    trainer.setImageUrl(fileName);
                }

            } catch (IOException e) {

                e.printStackTrace();

                return "redirect:/admin/trainers/new";
            }
        }

        trainerService.saveTrainer(trainer);

        return "redirect:/admin/trainers";
    }


    // =========================
    // ADMIN - EDIT TRAINER PAGE
    // =========================

    @GetMapping("/admin/trainers/{id}/edit")
    public String editTrainer(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        Trainer trainer =
                trainerService.getTrainerById(id).orElse(null);

        if (trainer == null) {
            return "redirect:/admin/trainers";
        }

        model.addAttribute(
                "trainer",
                trainer
        );

        return "admin/edit-trainer";
    }


    // =========================
    // ADMIN - UPDATE TRAINER
    // =========================

    @PostMapping("/admin/trainers/{id}/edit")
    public String updateTrainer(
            @PathVariable Integer id,
            @ModelAttribute Trainer trainer,
            @RequestParam("imageFile") MultipartFile imageFile,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        Trainer existingTrainer =
                trainerService.getTrainerById(id).orElse(null);

        if (existingTrainer == null) {
            return "redirect:/admin/trainers";
        }

        existingTrainer.setName(
                trainer.getName()
        );

        existingTrainer.setEmail(
                trainer.getEmail()
        );

        existingTrainer.setPhone(
                trainer.getPhone()
        );

        existingTrainer.setSpecialization(
                trainer.getSpecialization()
        );

        existingTrainer.setExperienceYears(
                trainer.getExperienceYears()
        );

        existingTrainer.setDescription(
                trainer.getDescription()
        );


        // Only replace image if a new image was selected
        if (imageFile != null && !imageFile.isEmpty()) {

            try {

                String fileName =
                        imageFile.getOriginalFilename();

                if (fileName != null && !fileName.isBlank()) {

                    Path uploadPath = Paths.get(
                            "src/main/resources/static/assets"
                    );

                    Files.createDirectories(uploadPath);

                    Path filePath =
                            uploadPath.resolve(fileName);

                    Files.copy(
                            imageFile.getInputStream(),
                            filePath,
                            StandardCopyOption.REPLACE_EXISTING
                    );

                    existingTrainer.setImageUrl(
                            fileName
                    );
                }

            } catch (IOException e) {

                e.printStackTrace();

                return "redirect:/admin/trainers/"
                        + id
                        + "/edit";
            }
        }


        trainerService.saveTrainer(
                existingTrainer
        );

        return "redirect:/admin/trainers";
    }


    // =========================
    // ADMIN - DELETE TRAINER
    // =========================

    @PostMapping("/admin/trainers/{id}/delete")
    public String deleteTrainer(
            @PathVariable Integer id,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        trainerService.deleteTrainer(id);

        return "redirect:/admin/trainers";
    }
}