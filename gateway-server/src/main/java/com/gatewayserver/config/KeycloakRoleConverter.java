package com.gatewayserver.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Map<String, Set<String>> realmAccess = (Map<String, Set<String>>) source.getClaims().get("realm_access");
        if(realmAccess==null||realmAccess.isEmpty()){
            return new ArrayList<>();
        }
        return realmAccess.get("roles")
                .stream().map(roleName->new SimpleGrantedAuthority("ROLE_"+roleName))
                .collect(Collectors.toSet());

    }

    /*@Override
    public <U> Converter<Jwt, U> andThen(Converter<? super Collection<GrantedAuthority>, ? extends U> after) {
        return Converter.super.andThen(after);
    }*/
}
