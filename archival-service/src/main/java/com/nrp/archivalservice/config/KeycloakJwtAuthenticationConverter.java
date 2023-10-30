package com.nrp.archivalservice.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final Converter<Jwt, Collection<GrantedAuthority>> delegate = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<GrantedAuthority> grantedAuthorities = extractRoles(jwt);
        Collection<GrantedAuthority> authorities = delegate.convert(jwt);
        if(authorities != null) {
            grantedAuthorities.addAll(authorities);
        }
        return new JwtAuthenticationToken(jwt, grantedAuthorities);
    }

    private List<GrantedAuthority> extractRoles(Jwt jwt) {
        /*Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return List.of();
        }*/

        List<String> roles = (List<String>) jwt.getClaims().get("roles");
        if (roles == null || roles.isEmpty()) {
            roles = List.of("ROLE_USER");
        }
        return roles.stream()
                .filter(role -> role.startsWith("ROLE_"))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}
