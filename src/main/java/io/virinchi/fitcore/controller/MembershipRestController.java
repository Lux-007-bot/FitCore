package io.virinchi.fitcore.controller;

import io.virinchi.fitcore.model.Membership;
import io.virinchi.fitcore.service.MembershipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// =========================================================
// REST API for Membership
// Same pattern as the other REST controllers. Note: Membership
// currently stores userId/planId as plain integers (not real
// JPA relationships yet), so this controller does the same —
// it does NOT validate that the user/plan actually exist.
// That's the same gap flagged in the "proper JPA relationships"
// item on the project status, and can be tightened later.
// =========================================================

@RestController
@RequestMapping("/api/memberships")
public class MembershipRestController {

    private final MembershipService membershipService;

    public MembershipRestController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    // GET /api/memberships
    @GetMapping
    public ResponseEntity<List<Membership>> getAllMemberships() {
        return ResponseEntity.ok(membershipService.getAllMemberships());
    }

    // GET /api/memberships/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Membership> getMembershipById(@PathVariable Integer id) {

        Optional<Membership> membership = membershipService.getMembershipById(id);

        return membership
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET /api/memberships/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Membership>> getMembershipsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(membershipService.getMembershipsByUserId(userId));
    }

    // POST /api/memberships
    @PostMapping
    public ResponseEntity<?> createMembership(@RequestBody Membership membership) {

        if (membership.getUserId() == null) {
            return ResponseEntity.badRequest().body("userId is required.");
        }

        if (membership.getPlanId() == null) {
            return ResponseEntity.badRequest().body("planId is required.");
        }

        if (membership.getStartDate() == null || membership.getEndDate() == null) {
            return ResponseEntity.badRequest().body("startDate and endDate are required.");
        }

        membership.setId(null);

        if (membership.getStatus() == null || membership.getStatus().isBlank()) {
            membership.setStatus("ACTIVE");
        }

        Membership saved = membershipService.saveMembership(membership);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/memberships/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMembership(@PathVariable Integer id, @RequestBody Membership membership) {

        Optional<Membership> existingOpt = membershipService.getMembershipById(id);

        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Membership existing = existingOpt.get();
        existing.setUserId(membership.getUserId());
        existing.setPlanId(membership.getPlanId());
        existing.setStartDate(membership.getStartDate());
        existing.setEndDate(membership.getEndDate());
        existing.setStatus(membership.getStatus());

        Membership updated = membershipService.saveMembership(existing);

        return ResponseEntity.ok(updated);
    }

    // DELETE /api/memberships/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMembership(@PathVariable Integer id) {

        Optional<Membership> existing = membershipService.getMembershipById(id);

        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        membershipService.deleteMembership(id);

        return ResponseEntity.noContent().build();
    }
}