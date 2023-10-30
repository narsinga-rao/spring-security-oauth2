package com.nrp.messageswebapp.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class MessageServiceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceClient.class);
    private static final String MESSAGE_SVC_BASE_URL = "http://localhost:8181";

    private static final String ARCHIVAL_SVC_BASE_URL = "http://localhost:8282";

    private final RestTemplate restTemplate;
    private final SecurityHelper securityHelper;

    public MessageServiceClient(RestTemplate restTemplate, SecurityHelper securityHelper) {
        this.restTemplate = restTemplate;
        this.securityHelper = securityHelper;
    }

    public List<Message> getMessages() {
        try {
            String endPoint = MESSAGE_SVC_BASE_URL + "/api/messages";

            ResponseEntity<List<Message>> listResponseEntity = restTemplate.exchange(
                    endPoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Message>>() {});
            return listResponseEntity.getBody();
        } catch (Exception e) {
            LOGGER.error("Error while fetching messages", e);
            return List.of();
        }
    }

    public void createMessage(Message message) {
        try {
            String endPoint = MESSAGE_SVC_BASE_URL + "/api/messages";
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", "Bearer " + securityHelper.getAccessToken());
            HttpEntity httpEntity = new HttpEntity<>(message, httpHeaders);
            ResponseEntity<Message> messageResponseEntity = restTemplate.exchange(
                    endPoint, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<Message>() {}
            );
            LOGGER.info("Create message response code {}", messageResponseEntity.getStatusCode());
        } catch (Exception e) {
            LOGGER.error("Error while creating message", e);
        }
    }

    public void archiveMessages() {
        try {
            String endPoint = ARCHIVAL_SVC_BASE_URL + "/api/messages/archive";
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", "Bearer " + securityHelper.getAccessToken());
            HttpEntity httpEntity = new HttpEntity(httpHeaders);
            ResponseEntity<Message> messageResponseEntity = restTemplate.exchange(
                    endPoint, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<Message>() {}
            );
            LOGGER.info("Archive messages response code: {}", messageResponseEntity.getStatusCode());
        } catch (Exception e) {
            LOGGER.error("Error while invoking archive messages", e);
        }
    }

}
