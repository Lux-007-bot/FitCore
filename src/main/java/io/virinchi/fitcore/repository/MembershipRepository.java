package io.virinchi.fitcore.repository;

import io.virinchi.fitcore.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Integer> {

    List<Membership> findByUserId(Integer userId);

    long countByStatus(String status);

    @Query("""
            SELECT m
            FROM Membership m
            WHERE m.startDate >= :startDate
            """)
    List<Membership> findMembershipsFromDate(LocalDate startDate);

    @Query("""
            SELECT m.planId, COUNT(m)
            FROM Membership m
            GROUP BY m.planId
            """)
    List<Object[]> countMembershipsByPlan();
}