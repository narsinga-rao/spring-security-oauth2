package com.nrp.messagesservice.repository;

import com.nrp.messagesservice.domain.Message;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MessageRepository {

    private static final AtomicLong ID = new AtomicLong(0L);
    private static final List<Message> MESSAGES = new ArrayList<>();

    public List<Message> getMessages() {
        return MESSAGES;
    }

    @PostConstruct
    void init() {
        getDefaultMessages().forEach(message -> {
            message.setId(ID.incrementAndGet());
            MESSAGES.add(message);
        });
    }

    public Message createMessage(Message message) {
        message.setId(ID.incrementAndGet());
        message.setCreatedAt(Instant.now());
        MESSAGES.add(message);
        return message;
    }

    private List<Message> getDefaultMessages() {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(null, "Test Message 1", "admin", Instant.now()));
        messages.add(new Message(null, "Test Message 2", "admin", Instant.now()));
        return messages;
    }

}
