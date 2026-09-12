package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.Membership;
import io.virinchi.fitcore.model.MembershipPlan;
import io.virinchi.fitcore.model.User;
import io.virinchi.fitcore.service.MembershipPlanService;
import io.virinchi.fitcore.service.MembershipService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MembershipPlanController {


    private final MembershipPlanService membershipPlanService;
    private final MembershipService membershipService;

    public MembershipPlanController(
            MembershipPlanService membershipPlanService,
            MembershipService membershipService) {

        this.membershipPlanService = membershipPlanService;
        this.membershipService = membershipService;
    }

    @GetMapping("/membership")
    public String membership(
            HttpSession session,
            Model model) {

        model.addAttribute(
                "plans",
                membershipPlanService.getAllPlans()
        );

        User user =
                (User) session.getAttribute("loggedInUser");

        if (user != null) {

            List<Membership> memberships =
                    membershipService.getMembershipsByUserId(user.getId());

            if (!memberships.isEmpty()) {

                Membership currentMembership =
                        memberships.get(memberships.size() - 1);

                MembershipPlan currentPlan =
                        membershipPlanService
                                .getPlanById(currentMembership.getPlanId())
                                .orElse(null);

                model.addAttribute(
                        "currentMembership",
                        currentMembership
                );

                model.addAttribute(
                        "currentPlan",
                        currentPlan
                );
            }
        }

        return "membership";
    }

    @GetMapping("/admin/membership-plans")
    public String manageMembershipPlans(
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "plans",
                membershipPlanService.getAllPlans()
        );

        return "admin/membership-plans";
    }

    @GetMapping("/admin/membership-plans/new")
    public String newMembershipPlan(
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "plan",
                new MembershipPlan()
        );

        return "admin/add-membership-plan";
    }

    @PostMapping("/admin/membership-plans/new")
    public String createMembershipPlan(
            @RequestParam String name,
            @RequestParam Integer durationMonths,
            @RequestParam Double price,
            @RequestParam String description,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/login";
        }

        MembershipPlan plan = new MembershipPlan();

        plan.setName(name);
        plan.setDurationMonths(durationMonths);
        plan.setPrice(price);
        plan.setDescription(description);

        membershipPlanService.savePlan(plan);

        return "redirect:/admin/membership-plans";
    }

}
