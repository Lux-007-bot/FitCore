package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.Admin;
import io.virinchi.fitcore.model.ContactMessage;
import io.virinchi.fitcore.repository.AdminRepository;
import io.virinchi.fitcore.repository.MembershipPlanRepository;
import io.virinchi.fitcore.repository.MembershipRepository;
import io.virinchi.fitcore.repository.UserRepository;
import io.virinchi.fitcore.service.ContactMessageService;
import io.virinchi.fitcore.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AdminController {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final ContactMessageService contactMessageService;
    private final EmailService emailService;

    public AdminController(
            AdminRepository adminRepository,
            UserRepository userRepository,
            MembershipRepository membershipRepository,
            MembershipPlanRepository membershipPlanRepository,
            ContactMessageService contactMessageService,
            EmailService emailService) {

        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.membershipPlanRepository = membershipPlanRepository;
        this.contactMessageService = contactMessageService;
        this.emailService = emailService;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(
            HttpSession session,
            Model model) {

        Admin admin =
                (Admin) session.getAttribute("loggedInAdmin");

        if (admin == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "totalUsers",
                userRepository.count()
        );

        model.addAttribute(
                "totalMemberships",
                membershipRepository.countByStatus("ACTIVE")
        );

        model.addAttribute(
                "totalPlans",
                membershipPlanRepository.count()
        );

        model.addAttribute(
                "totalAdmins",
                adminRepository.count()
        );

        model.addAttribute(
                "totalContactMessages",
                contactMessageService.getAllMessages().size()
        );

        long unreadCount = contactMessageService.getAllMessages()
                .stream()
                .filter(m -> "UNREAD".equals(m.getStatus()))
                .count();

        model.addAttribute("unreadContactMessages", unreadCount);

        return "admin/adminDashboard";
    }

    @GetMapping("/admin/contact-messages")
    public String contactMessages(HttpSession session, Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "messages",
                contactMessageService.getAllMessages()
        );

        return "admin/contact-messages";
    }

    @GetMapping("/admin/contact-messages/{id}")
    public String contactMessageDetail(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        Optional<ContactMessage> messageOpt =
                contactMessageService.getMessageById(id);

        if (messageOpt.isEmpty()) {
            return "redirect:/admin/contact-messages";
        }

        model.addAttribute("message", messageOpt.get());

        return "admin/contact-message-detail";
    }

    @PostMapping("/admin/contact-messages/{id}/reply")
    public String replyToContactMessage(
            @PathVariable Integer id,
            @RequestParam String reply,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        ContactMessage updated =
                contactMessageService.replyToMessage(id, reply);

        try {
            emailService.sendContactReplyToUser(
                    updated.getEmail(),
                    updated.getName(),
                    updated.getSubject(),
                    updated.getMessage(),
                    reply
            );
        } catch (RuntimeException e) {
            // Reply is already saved either way — email is a bonus,
            // not a requirement for the reply to count as sent.
        }

        return "redirect:/admin/contact-messages/" + id;
    }

    @PostMapping("/admin/contact-messages/{id}/delete")
    public String deleteContactMessage(
            @PathVariable Integer id,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        contactMessageService.deleteMessage(id);

        return "redirect:/admin/contact-messages";
    }

    @GetMapping("/admin/membership-management")
    public String membershipManagement(HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        return "admin/membership-management";
    }

    @GetMapping("/admin/logout")
    public String adminLogout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }

}