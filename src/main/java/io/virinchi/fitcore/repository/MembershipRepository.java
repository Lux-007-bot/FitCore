package io.virinchi.fitcore.repository;

import io.virinchi.fitcore.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Integer> {

    List<Membership> findByUserId(Integer userId);
}