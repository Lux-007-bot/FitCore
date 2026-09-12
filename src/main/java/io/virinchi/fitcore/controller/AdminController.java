package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.Admin;
import io.virinchi.fitcore.repository.AdminRepository;
import io.virinchi.fitcore.repository.MembershipPlanRepository;
import io.virinchi.fitcore.repository.MembershipRepository;
import io.virinchi.fitcore.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository membershipPlanRepository;

    public AdminController(
            AdminRepository adminRepository,
            UserRepository userRepository,
            MembershipRepository membershipRepository,
            MembershipPlanRepository membershipPlanRepository) {

        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.membershipPlanRepository = membershipPlanRepository;
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

        return "admin/adminDashboard";
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
