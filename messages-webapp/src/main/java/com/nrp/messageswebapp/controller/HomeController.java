package com.nrp.messageswebapp.controller;

import com.nrp.messageswebapp.domain.Message;
import com.nrp.messageswebapp.domain.MessageServiceClient;
import com.nrp.messageswebapp.domain.SecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
    private final MessageServiceClient messageServiceClient;

    public HomeController(MessageServiceClient messageServiceClient) {
        this.messageServiceClient = messageServiceClient;
    }

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User != null) {
            model.addAttribute("username", oAuth2User.getAttribute("name"));
        } else {
            model.addAttribute("username", "Guest");
        }

        List<Message> messages = messageServiceClient.getMessages();
        LOGGER.info("Message count: {}", messages.size());
        model.addAttribute("messages", messages);
        return "home";
    }

    @PostMapping("/messages")
    public String createMessage(Message message) {
        Map<String, Object> loginUserDetails = SecurityHelper.getLoginDetails();
        LOGGER.info("loginUserDetails: {}", loginUserDetails);
        message.setCreatedBy(loginUserDetails.get("username").toString());
        messageServiceClient.createMessage(message);
        return "redirect:/";
    }

    @PostMapping("/messages/archive")
    public String archiveMessages() {
        messageServiceClient.archiveMessages();
        return "redirect:/";
    }
}
