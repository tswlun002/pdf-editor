package com.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private  final String[] PATH_TO_AUTHENTICATE={"pdf-editor/users/**","pdf-editor/documents/**","pdf-editor/emails/**"};

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity){
        httpSecurity.
                authorizeExchange(exchange->
                                exchange.pathMatchers(HttpMethod.GET).permitAll()
                                .pathMatchers(PATH_TO_AUTHENTICATE).hasAnyRole(Roles.ADMIN.name(),Roles.CLIENT.name())


                )
                .oauth2ResourceServer(oauth2Server->oauth2Server.jwt(jwtSpec ->
                                jwtSpec.jwtAuthenticationConverter(grantedAuth())));
        httpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable);
        return httpSecurity.build();
    }

    private Converter<Jwt,? extends Mono<? extends AbstractAuthenticationToken>> grantedAuth() {
        var jwtConv= new JwtAuthenticationConverter();
        jwtConv.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtConv);
    }
}
