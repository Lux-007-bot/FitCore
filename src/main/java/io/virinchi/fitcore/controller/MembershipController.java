        package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.Membership;
import io.virinchi.fitcore.model.MembershipPlan;
import io.virinchi.fitcore.model.User;
import io.virinchi.fitcore.service.MembershipPlanService;
import io.virinchi.fitcore.service.MembershipService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class MembershipController {

    private final MembershipService membershipService;
    private final MembershipPlanService membershipPlanService;

    public MembershipController(
            MembershipService membershipService,
            MembershipPlanService membershipPlanService) {

        this.membershipService = membershipService;
        this.membershipPlanService = membershipPlanService;
    }


    // Show membership form
    @GetMapping("/join")
    public String join(
            @RequestParam(required = false) String plan,
            HttpSession session,
            Model model) {

        // Check if user is logged in
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        // Load all membership plans
        model.addAttribute(
                "plans",
                membershipPlanService.getAllPlans()
        );

        // Find selected plan from URL
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

        // Get logged-in user
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            return "redirect:/login";
        }

        // Get selected membership plan
        MembershipPlan plan =
                membershipPlanService.getPlanById(planId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Membership plan not found"
                                )
                        );

        // Convert selected date
        LocalDate startDate = LocalDate.parse(start);

        // Calculate membership end date
        LocalDate endDate =
                startDate.plusMonths(plan.getDurationMonths());

        // Create membership
        Membership membership = new Membership();

        membership.setUserId(user.getId());
        membership.setPlanId(plan.getId());
        membership.setStartDate(startDate);
        membership.setEndDate(endDate);
        membership.setStatus("ACTIVE");

        // Save membership to database
        membershipService.saveMembership(membership);

        // Store information for success page
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

        // Check login
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
}
