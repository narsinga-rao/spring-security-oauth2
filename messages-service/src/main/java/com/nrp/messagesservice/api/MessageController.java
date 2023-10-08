package com.nrp.messagesservice.api;

import com.nrp.messagesservice.domain.Message;
import com.nrp.messagesservice.repository.MessageRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    private final MessageRepository messageRepository;

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping
    List<Message> getMessages() {
        return messageRepository.getMessages();
    }

    @PostMapping
    Message createMessage(@RequestBody @Valid Message message) {
        return messageRepository.createMessage(message);
    }

    @PostMapping("/archive")
    Map<String, String> archiveMessages() {
        LOGGER.info("Archiving all messages");
        return Map.of("status", "Success");
    }
}
