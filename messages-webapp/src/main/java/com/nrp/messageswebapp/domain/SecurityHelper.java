package com.nrp.messageswebapp.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SecurityHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityHelper.class);
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    public SecurityHelper(OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
    }

    public String getAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken)) {
            return null;
        }
        OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(
                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(), oAuth2AuthenticationToken.getName());
        LOGGER.info("Access Token: {}", oAuth2AuthorizedClient.getAccessToken().getTokenValue());
        return oAuth2AuthorizedClient.getAccessToken().getTokenValue();
    }

    public static Map<String, Object> getLoginDetails() {
        Map<String, Object> loginDetailsMap = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof OAuth2AuthenticationToken)) {
            return  null;
        }

        DefaultOidcUser principal = (DefaultOidcUser) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();
        List<String> roles = grantedAuthorities.stream().map(GrantedAuthority::getAuthority).toList();
        OidcUserInfo userInfo = principal.getUserInfo();
        LOGGER.info("User Info: {}", userInfo);
        loginDetailsMap.put("id", userInfo.getSubject());
        loginDetailsMap.put("fullName", userInfo.getFullName());
        loginDetailsMap.put("email", userInfo.getEmail());
        //loginDetailsMap.put("username", userInfo.getPreferredUsername());
        loginDetailsMap.put("username", userInfo.getClaims().get("sub"));
        loginDetailsMap.put("roles", roles);

        return loginDetailsMap;
    }

}
