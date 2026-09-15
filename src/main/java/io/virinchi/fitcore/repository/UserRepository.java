package io.virinchi.fitcore.repository;

import io.virinchi.fitcore.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Page<User> findAll(Pageable pageable);

    Page<User> findByFullnameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String fullname,
            String email,
            Pageable pageable
    );

    long countByActive(boolean active);

    @Query("""
            SELECT u.active, COUNT(u)
            FROM User u
            GROUP BY u.active
            """)
    List<Object[]> countUsersByActive();
}