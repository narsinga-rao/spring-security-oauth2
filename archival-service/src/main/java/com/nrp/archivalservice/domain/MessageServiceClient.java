package com.nrp.archivalservice.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MessageServiceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceClient.class);
    private static final String MESSAGE_SVC_URL = "http://localhost:8181";

    private final SecurityHelper securityHelper;
    private final RestTemplate restTemplate;

    public MessageServiceClient(SecurityHelper securityHelper, RestTemplate restTemplate) {
        this.securityHelper = securityHelper;
        this.restTemplate = restTemplate;
    }

    public void archiveMessages() {
        try {
            String url = MESSAGE_SVC_URL + "/api/messages/archive";
            OAuth2AccessToken oAuth2AccessToken = securityHelper.getOAuth2AccessToken();
            String accessToken = oAuth2AccessToken.getTokenValue();
            LOGGER.info("accessToken: {}", accessToken);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", "Bearer " + accessToken);
            HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
            ResponseEntity<Void> responseEntity = restTemplate.exchange(
                    url, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<>() {}
            );
            LOGGER.info("Archive message response code: {}", responseEntity.getStatusCode());
        } catch (Exception e) {
            LOGGER.error("Error while invoking archive messages API", e);
        }
    }

}
