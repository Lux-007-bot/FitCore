package io.virinchi.fitcore.service;

import io.virinchi.fitcore.model.MembershipPlan;
import io.virinchi.fitcore.repository.MembershipPlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;

    public MembershipPlanService(MembershipPlanRepository membershipPlanRepository) {
        this.membershipPlanRepository = membershipPlanRepository;
    }

    public List<MembershipPlan> getAllPlans() {
        return membershipPlanRepository.findAll();
    }

    public Optional<MembershipPlan> getPlanById(Integer id) {
        return membershipPlanRepository.findById(id);
    }

    public MembershipPlan savePlan(MembershipPlan plan) {
        return membershipPlanRepository.save(plan);
    }

    public void deletePlan(Integer id) {
        membershipPlanRepository.deleteById(id);
    }
}