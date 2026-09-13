package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.Trainer;
import io.virinchi.fitcore.service.TrainerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

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

    @GetMapping("/trainer/{id}/image")
    @ResponseBody
    public byte[] getTrainerImage(
            @PathVariable Integer id) {

        Trainer trainer =
                trainerService.getTrainerById(id).orElse(null);

        if (trainer == null || trainer.getImageData() == null) {
            return new byte[0];
        }

        return trainer.getImageData();
    }

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

    @PostMapping("/admin/trainers/new")
    public String createTrainer(
            @ModelAttribute Trainer trainer,
            @RequestParam("imageFile") MultipartFile imageFile,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        if (imageFile != null && !imageFile.isEmpty()) {

            try {
                trainer.setImageData(
                        imageFile.getBytes()
                );

                trainer.setImageType(
                        imageFile.getContentType()
                );

            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/admin/trainers/new";
            }
        }

        trainerService.saveTrainer(trainer);

        return "redirect:/admin/trainers";
    }

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

        if (imageFile != null && !imageFile.isEmpty()) {

            try {
                existingTrainer.setImageData(
                        imageFile.getBytes()
                );

                existingTrainer.setImageType(
                        imageFile.getContentType()
                );

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