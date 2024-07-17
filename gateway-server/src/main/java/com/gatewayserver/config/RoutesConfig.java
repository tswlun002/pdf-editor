package com.gatewayserver.config;

import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.time.Duration;

@Configuration
public class RoutesConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes().
                route(predicateSpec -> predicateSpec.path("/pdf-editor/users/**").
                        filters(filterSpec->
                        {

                          return   filterSpec.rewritePath("/pdf-editor/users/(?<segment>.*)", "/pdf-editor/users/${segment}")
                                    .circuitBreaker(circuitConfi ->
                                            circuitConfi.setName("userServiceCircuitBreaker")
                                                    .setFallbackUri("/pdf-editor/fallback/contact-team")
                                    );

                        })
                        .uri("lb://USERS"))
                .route(predicateSpec -> predicateSpec.path("/pdf-editor/documents/**").
                        filters(filterSpec->filterSpec.rewritePath("/pdf-editor/documents/(?<segment>.*)","/pdf-editor/documents/${segment}")
                                .circuitBreaker(circuitConfi->
                                        circuitConfi.setName("documentsCircuitBreaker")
                                                .setFallbackUri("/pdf-editor/fallback/contact-team")
                                                .addStatusCode(HttpStatus.SERVICE_UNAVAILABLE.name())
                                ))
                        .uri("lb://DOCUMENTS"))
                .route(predicateSpec -> predicateSpec.path("/pdf-editor/emails/**").
                        filters(filterSpec->filterSpec.rewritePath("/pdf-editor/emails/(?<segment>.*)","/pdf-editor/email/${segment}")
                                .circuitBreaker(circuitConfi->
                                        circuitConfi.setName("emailCircuitBreaker")
                                                .setFallbackUri("/pdf-editor/fallback/contact-team")
                                                .addStatusCode(HttpStatus.SERVICE_UNAVAILABLE.name())
                                ))
                        .uri("lb://EMAIL"))

                .build();
    }
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> circuitBreakerFactoryCustomizer(){
        return factory->factory.configureDefault(
                id->new Resilience4JConfigBuilder(id)
                        .timeLimiterConfig(
                                TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(10)).build()
                        )
                        .build() );
    }
}
