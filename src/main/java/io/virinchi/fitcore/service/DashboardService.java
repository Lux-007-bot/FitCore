package io.virinchi.fitcore.service;

import io.virinchi.fitcore.model.Membership;
import io.virinchi.fitcore.model.MembershipPlan;
import io.virinchi.fitcore.repository.ContactMessageRepository;
import io.virinchi.fitcore.repository.MembershipPlanRepository;
import io.virinchi.fitcore.repository.MembershipRepository;
import io.virinchi.fitcore.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

// =========================================================
// Aggregates real data from existing repositories into
// chart-ready label/value pairs for the admin dashboard.
// No new database columns or fake data — everything here
// is computed from data that already exists.
// =========================================================

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final ContactMessageRepository contactMessageRepository;

    public DashboardService(
            UserRepository userRepository,
            MembershipRepository membershipRepository,
            MembershipPlanRepository membershipPlanRepository,
            ContactMessageRepository contactMessageRepository) {

        this.userRepository = userRepository;
        this.membershipRepository = membershipRepository;
        this.membershipPlanRepository = membershipPlanRepository;
        this.contactMessageRepository = contactMessageRepository;
    }

    // ---------- Active vs Inactive Users ----------

    public Map<String, Long> getUserStatusBreakdown() {

        Map<String, Long> result =
                new LinkedHashMap<>();

        result.put("Active", 0L);
        result.put("Inactive", 0L);

        List<Object[]> rows =
                userRepository.countUsersByActive();

        for (Object[] row : rows) {

            Boolean active = (Boolean) row[0];
            Long count = (Long) row[1];

            if (active) {
                result.put("Active", count);
            } else {
                result.put("Inactive", count);
            }
        }

        return result;
    }

    // ---------- Memberships grouped by plan name ----------

    public Map<String, Long> getMembershipsByPlan() {

        Map<Integer, String> planNamesById =
                membershipPlanRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(
                                MembershipPlan::getId,
                                MembershipPlan::getName
                        ));

        Map<String, Long> counts =
                new LinkedHashMap<>();

        List<Object[]> results =
                membershipRepository.countMembershipsByPlan();

        for (Object[] row : results) {

            Integer planId = (Integer) row[0];
            Long count = (Long) row[1];

            String planName =
                    planNamesById.getOrDefault(
                            planId,
                            "Unknown Plan"
                    );

            counts.put(planName, count);
        }

        return counts;
    }

    // ---------- New memberships per month, last 6 months ----------

    public Map<String, Long> getMembershipTrend() {

        DateTimeFormatter labelFormat =
                DateTimeFormatter.ofPattern("MMM yyyy");

        Map<String, Long> trend =
                new LinkedHashMap<>();

        YearMonth current = YearMonth.now();

        List<YearMonth> lastSixMonths =
                new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            lastSixMonths.add(current.minusMonths(i));
        }

        LocalDate startDate =
                lastSixMonths.get(0).atDay(1);

        List<Membership> memberships =
                membershipRepository.findMembershipsFromDate(startDate);

        for (YearMonth ym : lastSixMonths) {
            trend.put(
                    ym.format(labelFormat),
                    0L
            );
        }

        for (Membership membership : memberships) {

            LocalDate start =
                    membership.getStartDate();

            if (start == null) {
                continue;
            }

            YearMonth membershipMonth =
                    YearMonth.from(start);

            String label =
                    membershipMonth.format(labelFormat);

            if (trend.containsKey(label)) {
                trend.merge(
                        label,
                        1L,
                        Long::sum
                );
            }
        }

        return trend;
    }

    // ---------- Contact messages: unread vs replied ----------

    public Map<String, Long> getMessageStatusBreakdown() {

        Map<String, Long> result =
                new LinkedHashMap<>();

        result.put("Unread", 0L);
        result.put("Replied", 0L);

        List<Object[]> rows =
                contactMessageRepository.countMessagesByStatus();

        for (Object[] row : rows) {

            String status = (String) row[0];
            Long count = (Long) row[1];

            if ("UNREAD".equals(status)) {
                result.put("Unread", count);
            }

            if ("REPLIED".equals(status)) {
                result.put("Replied", count);
            }
        }

        return result;
    }
    }
