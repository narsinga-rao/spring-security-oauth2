package com.nrp.authserver.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class OAuth2TokenCustomizerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2TokenCustomizerConfig.class);

    @Value("${spring.security.user.roles}")
    private List<String> roles;

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        LOGGER.info("Coming into jwtCustomizer");
        return context -> {
            if (context.getTokenType() == OAuth2TokenType.ACCESS_TOKEN) {
                LOGGER.info("Its a ACCESS_TOKEN");
                Authentication authentication = context.getPrincipal();
                Set<String> authorities = authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());
                LOGGER.info("Authorities: {}", authorities);
                if (authentication instanceof OAuth2ClientAuthenticationToken && List.of("messages-webapp", "archival-service").contains(authentication.getName()) && authorities.isEmpty()) {
                    authorities = roles.stream().map(s -> "ROLE_" + s).collect(Collectors.toSet());
                    LOGGER.info("Authorities for Client: {}", authorities);
                }
                context.getClaims().claim("roles", authorities);
                context.getClaims().claim("authorities", authorities);
            } else if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                //OidcUserInfo userInfo = userInfoService.loadUser(context.getPrincipal().getName());
                //context.getClaims().claims(claims -> claims.putAll(userInfo.getClaims()));
                Authentication authentication = context.getPrincipal();
                Set<String> authorities = authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet());
                LOGGER.info("Oidc Authorities: {}", authorities);
                context.getClaims().claim("roles", roles.stream().map(s -> "ROLE_" + s).collect(Collectors.toSet()));
            }
        };
    }



}
