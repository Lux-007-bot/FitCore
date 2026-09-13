package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.ContactMessage;
import io.virinchi.fitcore.model.User;
import io.virinchi.fitcore.service.ContactMessageService;
import io.virinchi.fitcore.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ContactController {

    private final ContactMessageService contactMessageService;
    private final EmailService emailService;

    public ContactController(
            ContactMessageService contactMessageService,
            EmailService emailService) {

        this.contactMessageService = contactMessageService;
        this.emailService = emailService;
    }

    @GetMapping("/contact")
    public String contactPage(Model model, HttpSession session) {

        User loggedInUser =
                (User) session.getAttribute("loggedInUser");

        model.addAttribute("loggedInUser", loggedInUser);

        if (loggedInUser != null) {
            model.addAttribute(
                    "myMessages",
                    contactMessageService.getMessagesByUser(loggedInUser)
            );
        }

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

        try {
            emailService.sendContactNotificationToAdmin(
                    name, email, subject, message
            );
        } catch (RuntimeException e) {
            // Message is already saved in the DB either way, so the
            // admin will still see it on the dashboard even if the
            // email notification itself fails (e.g. SMTP hiccup).
        }

        model.addAttribute(
                "successMessage",
                "Thank you! Your message has been sent successfully."
        );

        model.addAttribute("loggedInUser", loggedInUser);

        if (loggedInUser != null) {
            model.addAttribute(
                    "myMessages",
                    contactMessageService.getMessagesByUser(loggedInUser)
            );
        }

        return "contact";
    }
}