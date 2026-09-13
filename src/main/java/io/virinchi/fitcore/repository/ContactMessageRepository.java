package io.virinchi.fitcore.repository;

import io.virinchi.fitcore.model.ContactMessage;
import io.virinchi.fitcore.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactMessageRepository
        extends JpaRepository<ContactMessage, Integer> {

    List<ContactMessage> findByUser(User user);

    List<ContactMessage> findAllByOrderByCreatedAtDesc();
}