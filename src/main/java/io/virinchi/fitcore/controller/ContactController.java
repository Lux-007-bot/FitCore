package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.ContactMessage;
import io.virinchi.fitcore.model.User;
import io.virinchi.fitcore.service.ContactMessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ContactController {

    private final ContactMessageService contactMessageService;

    public ContactController(ContactMessageService contactMessageService) {
        this.contactMessageService = contactMessageService;
    }

    @GetMapping("/contact")
    public String contactPage(Model model, HttpSession session) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        model.addAttribute("loggedInUser", loggedInUser);

        return "contact";
    }

    @PostMapping("/contact")
    public String submitContact(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String subject,
            @RequestParam String message,
            HttpSession session,
            Model model) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        ContactMessage contactMessage = new ContactMessage();

        contactMessage.setName(name);
        contactMessage.setEmail(email);
        contactMessage.setSubject(subject);
        contactMessage.setMessage(message);

        // Connect message to logged-in user
        if (loggedInUser != null) {
            contactMessage.setUser(loggedInUser);
        }

        contactMessageService.saveMessage(contactMessage);

        model.addAttribute(
                "successMessage",
                "Thank you! Your message has been sent successfully."
        );

        model.addAttribute("loggedInUser", loggedInUser);

        return "contact";
    }
}