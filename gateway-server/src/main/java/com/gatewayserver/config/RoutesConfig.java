package com.gatewayserver.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes().
                route(predicateSpec -> predicateSpec.path("/pdf-editor/users/**").
                        filters(filterSpec->filterSpec.rewritePath("/pdf-editor/users/(?<segment>.*)","/pdf-editor/users/${segment}")
                                .circuitBreaker(circuitConfi->circuitConfi.setName("userServiceCircuitBreaker")))
                        .uri("lb://USERS"))
                .route(predicateSpec -> predicateSpec.path("/pdf-editor/documents/**").
                        filters(filterSpec->filterSpec.rewritePath("/pdf-editor/documents/(?<segment>.*)","/pdf-editor/documents/${segment}"))
                        .uri("lb://DOCUMENTS"))
                .route(predicateSpec -> predicateSpec.path("/pdf-editor/emails/**").
                        filters(filterSpec->filterSpec.rewritePath("/pdf-editor/emails/(?<segment>.*)","/pdf-editor/email/${segment}"))
                        .uri("lb://EMAIL"))

                .build();
    }
}
