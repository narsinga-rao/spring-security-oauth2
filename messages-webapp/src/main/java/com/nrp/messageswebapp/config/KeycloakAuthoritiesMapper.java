package com.nrp.messageswebapp.config;

import com.nrp.messageswebapp.domain.SecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class KeycloakAuthoritiesMapper implements GrantedAuthoritiesMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakAuthoritiesMapper.class);

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        LOGGER.info("Coming into mapAuthorities");
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        authorities.forEach(grantedAuthority -> {
            if(grantedAuthority instanceof SimpleGrantedAuthority) {
                LOGGER.info("In grantedAuthority instanceof SimpleGrantedAuthority: {}", grantedAuthority);
                grantedAuthorities.add(grantedAuthority);
            } else if (grantedAuthority instanceof OidcUserAuthority oidcUserAuthority) {
                LOGGER.info("In grantedAuthority instanceof OidcUserAuthority");
                OidcIdToken oidcIdToken = oidcUserAuthority.getIdToken();
                Map<String, Object> claims = oidcIdToken.getClaims();
                //Map<String, Object> realm_access = (Map<String, Object>) claims.get("realm_access");
                List<String> roles = (List<String>) claims.get("roles");
                if (roles != null && !roles.isEmpty()) {
                    //List<String> roles = (List<String>) realm_access.get("roles");
                    var list = roles.stream()
                            .filter(s -> s.startsWith("ROLE_"))
                            .map(SimpleGrantedAuthority::new).toList();
                    grantedAuthorities.addAll(list);
                }
            } else if (grantedAuthority instanceof OAuth2UserAuthority oAuth2UserAuthority) {
                Map<String, Object> userAttributes = oAuth2UserAuthority.getAttributes();
                LOGGER.info("OAuth2UserAuthority User attributes: {}", userAttributes);
            }
        });
        return grantedAuthorities;
    }

    private List<GrantedAuthority> extractRoles(Jwt jwt) {

        List<String> roles = (List<String>) jwt.getClaims().get("roles");
        LOGGER.info("Roles....: {}", roles);
        if (roles == null || roles.isEmpty()) {
            roles = List.of("ROLE_USER");
        }
        return roles.stream()
                .filter(role -> role.startsWith("ROLE_"))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
