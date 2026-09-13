package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.MembershipPlan;
import io.virinchi.fitcore.service.MembershipPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// =========================================================
// REST API for MembershipPlan
// Same pattern as TrainerRestController / UserRestController.
// Sits alongside MembershipPlanController (Thymeleaf pages),
// reuses the same MembershipPlanService.
// =========================================================

@RestController
@RequestMapping("/api/membership-plans")
public class MembershipPlanRestController {

    private final MembershipPlanService membershipPlanService;

    public MembershipPlanRestController(MembershipPlanService membershipPlanService) {
        this.membershipPlanService = membershipPlanService;
    }

    // GET /api/membership-plans
    @GetMapping
    public ResponseEntity<List<MembershipPlan>> getAllPlans() {
        return ResponseEntity.ok(membershipPlanService.getAllPlans());
    }

    // GET /api/membership-plans/{id}
    @GetMapping("/{id}")
    public ResponseEntity<MembershipPlan> getPlanById(@PathVariable Integer id) {

        Optional<MembershipPlan> plan = membershipPlanService.getPlanById(id);

        return plan
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/membership-plans
    @PostMapping
    public ResponseEntity<?> createPlan(@RequestBody MembershipPlan plan) {

        if (plan.getName() == null || plan.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Plan name is required.");
        }

        if (plan.getDurationMonths() == null || plan.getDurationMonths() <= 0) {
            return ResponseEntity.badRequest().body("Duration (months) must be a positive number.");
        }

        if (plan.getPrice() == null || plan.getPrice() < 0) {
            return ResponseEntity.badRequest().body("Price must be provided and cannot be negative.");
        }

        plan.setId(null);

        MembershipPlan saved = membershipPlanService.savePlan(plan);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/membership-plans/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlan(@PathVariable Integer id, @RequestBody MembershipPlan plan) {

        Optional<MembershipPlan> existingOpt = membershipPlanService.getPlanById(id);

        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MembershipPlan existing = existingOpt.get();
        existing.setName(plan.getName());
        existing.setDurationMonths(plan.getDurationMonths());
        existing.setPrice(plan.getPrice());
        existing.setDescription(plan.getDescription());

        MembershipPlan updated = membershipPlanService.savePlan(existing);

        return ResponseEntity.ok(updated);
    }

    // DELETE /api/membership-plans/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlan(@PathVariable Integer id) {

        Optional<MembershipPlan> existing = membershipPlanService.getPlanById(id);

        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        membershipPlanService.deletePlan(id);

        return ResponseEntity.noContent().build();
    }
}