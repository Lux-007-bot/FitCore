package io.virinchi.fitcore.service;

import io.virinchi.fitcore.model.Membership;
import io.virinchi.fitcore.repository.MembershipRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    public Membership saveMembership(Membership membership) {
        return membershipRepository.save(membership);
    }

    public List<Membership> getMembershipsByUserId(Integer userId) {
        return membershipRepository.findByUserId(userId);
    }

    public List<Membership> getAllMemberships() {
        return membershipRepository.findAll();
    }
}