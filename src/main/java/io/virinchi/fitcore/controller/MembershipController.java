package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.Membership;
import io.virinchi.fitcore.model.MembershipPlan;
import io.virinchi.fitcore.model.User;
import io.virinchi.fitcore.service.MembershipPlanService;
import io.virinchi.fitcore.service.MembershipService;
import io.virinchi.fitcore.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class MembershipController {


    private final MembershipService membershipService;
    private final MembershipPlanService membershipPlanService;
    private final UserService userService;

    public MembershipController(
            MembershipService membershipService,
            MembershipPlanService membershipPlanService,
            UserService userService) {

        this.membershipService = membershipService;
        this.membershipPlanService = membershipPlanService;
        this.userService = userService;
    }

    // Show membership form
    @GetMapping("/join")
    public String join(
            @RequestParam(required = false) String plan,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "plans",
                membershipPlanService.getAllPlans()
        );

        if (plan != null) {

            membershipPlanService.getAllPlans()
                    .stream()
                    .filter(p -> p.getName().equalsIgnoreCase(plan))
                    .findFirst()
                    .ifPresent(selectedPlan ->
                            model.addAttribute(
                                    "selectedPlan",
                                    selectedPlan
                            )
                    );
        }

        return "join";
    }

    // Process membership form
    @PostMapping("/join")
    public String joinMembership(
            @RequestParam Integer planId,
            @RequestParam String start,
            HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        MembershipPlan plan =
                membershipPlanService.getPlanById(planId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Membership plan not found"
                                )
                        );

        LocalDate startDate = LocalDate.parse(start);

        LocalDate endDate =
                startDate.plusMonths(plan.getDurationMonths());

        Membership membership = new Membership();

        membership.setUserId(user.getId());
        membership.setPlanId(plan.getId());
        membership.setStartDate(startDate);
        membership.setEndDate(endDate);
        membership.setStatus("ACTIVE");

        membershipService.saveMembership(membership);

        session.setAttribute(
                "newMembership",
                membership
        );

        session.setAttribute(
                "newMembershipPlan",
                plan
        );

        return "redirect:/membership-success";
    }

    // Membership success page
    @GetMapping("/membership-success")
    public String membershipSuccess(
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "membership",
                session.getAttribute("newMembership")
        );

        model.addAttribute(
                "plan",
                session.getAttribute("newMembershipPlan")
        );

        return "membership-success";
    }

    // Admin membership management
    @GetMapping("/admin/memberships")
    public String manageMemberships(
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        // Load all memberships
        model.addAttribute(
                "memberships",
                membershipService.getAllMemberships()
        );

        // Load all users
        model.addAttribute(
                "users",
                userService.getAllUsers()
        );

        return "admin/memberships";
    }

    // Admin edit membership page
    @GetMapping("/admin/memberships/{id}/edit")
    public String editMembership(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        Optional<Membership> membership =
                membershipService.getMembershipById(id);

        if (membership.isEmpty()) {
            return "redirect:/admin/memberships";
        }

        model.addAttribute(
                "membership",
                membership.get()
        );

        model.addAttribute(
                "plans",
                membershipPlanService.getAllPlans()
        );

        return "admin/edit-membership";
    }

    // Admin update membership
    @PostMapping("/admin/memberships/{id}/edit")
    public String updateMembership(
            @PathVariable Integer id,
            @RequestParam Integer planId,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String status,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        Optional<Membership> existingMembership =
                membershipService.getMembershipById(id);

        if (existingMembership.isEmpty()) {
            return "redirect:/admin/memberships";
        }

        Membership membership =
                existingMembership.get();

        membership.setPlanId(planId);

        membership.setStartDate(
                LocalDate.parse(startDate)
        );

        membership.setEndDate(
                LocalDate.parse(endDate)
        );

        membership.setStatus(status);

        membershipService.saveMembership(membership);

        return "redirect:/admin/memberships";
    }

    // Admin delete membership
    @PostMapping("/admin/memberships/{id}/delete")
    public String deleteMembership(
            @PathVariable Integer id,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        membershipService.deleteMembership(id);

        return "redirect:/admin/memberships";
    }

}
