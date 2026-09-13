package io.virinchi.fitcore.service;

import io.virinchi.fitcore.model.ContactMessage;
import io.virinchi.fitcore.model.User;
import io.virinchi.fitcore.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;

    public ContactMessageService(ContactMessageRepository contactMessageRepository) {
        this.contactMessageRepository = contactMessageRepository;
    }

    public ContactMessage saveMessage(ContactMessage contactMessage) {

        if (contactMessage.getCreatedAt() == null) {
            contactMessage.setCreatedAt(LocalDateTime.now());
        }

        if (contactMessage.getStatus() == null) {
            contactMessage.setStatus("UNREAD");
        }

        return contactMessageRepository.save(contactMessage);
    }

    public List<ContactMessage> getAllMessages() {
        return contactMessageRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<ContactMessage> getMessageById(Integer id) {
        return contactMessageRepository.findById(id);
    }

    public List<ContactMessage> getMessagesByUser(User user) {
        return contactMessageRepository.findByUser(user);
    }

    public ContactMessage replyToMessage(
            Integer id,
            String reply) {

        ContactMessage contactMessage =
                contactMessageRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Contact message not found"));

        contactMessage.setReply(reply);
        contactMessage.setStatus("REPLIED");
        contactMessage.setRepliedAt(LocalDateTime.now());

        return contactMessageRepository.save(contactMessage);
    }

    public void deleteMessage(Integer id) {
        contactMessageRepository.deleteById(id);
    }
}