package io.virinchi.fitcore.repository;

import io.virinchi.fitcore.model.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Integer> {
}