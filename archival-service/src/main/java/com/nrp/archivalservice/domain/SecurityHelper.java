package com.nrp.archivalservice.domain;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;

@Service
public class SecurityHelper {

    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

    public SecurityHelper(OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        this.oAuth2AuthorizedClientManager = oAuth2AuthorizedClientManager;
    }

    public OAuth2AccessToken getOAuth2AccessToken() {
        String clientRegistrationId = "archival-service";
        //OAuth2AuthorizeRequest oAuth2AuthorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId).principal("nrp").build();
        OAuth2AuthorizeRequest oAuth2AuthorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistrationId)
                .principal("archival-service").build();
        OAuth2AuthorizedClient oAuth2AuthorizedClient = this.oAuth2AuthorizedClientManager.authorize(oAuth2AuthorizeRequest);
        return oAuth2AuthorizedClient.getAccessToken();
    }
}
