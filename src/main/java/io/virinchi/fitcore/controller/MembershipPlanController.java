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
}