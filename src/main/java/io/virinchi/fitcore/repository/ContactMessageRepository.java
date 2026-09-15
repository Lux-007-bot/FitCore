package io.virinchi.fitcore.repository;

import io.virinchi.fitcore.model.ContactMessage;
import io.virinchi.fitcore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContactMessageRepository
        extends JpaRepository<ContactMessage, Integer> {

    List<ContactMessage> findByUser(User user);

    List<ContactMessage> findAllByOrderByCreatedAtDesc();

    long countByStatus(String status);

    @Query("""
            SELECT m.status, COUNT(m)
            FROM ContactMessage m
            GROUP BY m.status
            """)
    List<Object[]> countMessagesByStatus();
}