package com.example.keycloakdemo.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken(jwt, authorities);
    }
    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<String> realmRoles = List.of();
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
            realmRoles = roles.stream().map(Object::toString).toList();
        }
        List<String> clientRoles = List.of();
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            clientRoles = resourceAccess.values().stream()
                    .filter(Map.class::isInstance)
                    .map(Map.class::cast)
                    .filter(m -> m.get("roles") instanceof Collection<?> )
                    .flatMap(m -> ((Collection<?>) m.get("roles")).stream())
                    .map(Object::toString)
                    .toList();
        }
        Set<String> roles = Stream.concat(realmRoles.stream(), clientRoles.stream())
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .collect(Collectors.toSet());
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }
}
