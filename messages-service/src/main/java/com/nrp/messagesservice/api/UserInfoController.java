package com.nrp.messagesservice.api;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserInfoController {

    @GetMapping("/api/me")
    public Map<String, Object> currentUserDetails() {
        return getLoginUserDetails();
    }

    private Map<String, Object> getLoginUserDetails() {
        Map<String, Object> map = new HashMap<>();
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) jwtAuthenticationToken.getPrincipal();

        map.put("username", jwt.getClaimAsString("preferred_username"));
        map.put("email", jwt.getClaimAsString("email"));
        map.put("name", jwt.getClaimAsString("name"));
        map.put("token", jwt.getTokenValue());
        map.put("authorities", jwtAuthenticationToken.getAuthorities());
        map.put("roles", getRoles(jwt));

        return map;
    }

    private List<String> getRoles(Jwt jwt) {
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        if (realmAccess != null && !realmAccess.isEmpty()) {
            return (List<String>) realmAccess.get("roles");
        }
        return List.of();
    }

}
