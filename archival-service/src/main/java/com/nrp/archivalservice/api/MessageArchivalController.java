package com.nrp.archivalservice.api;

import com.nrp.archivalservice.domain.MessageServiceClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MessageArchivalController {

    private final MessageServiceClient messageServiceClient;

    public MessageArchivalController(MessageServiceClient messageServiceClient) {
        this.messageServiceClient = messageServiceClient;
    }

    @PostMapping("/api/messages/archive")
    Map<String, String> archiveMessages() {
        messageServiceClient.archiveMessages();
        return Map.of("status", "success");
    }

}
